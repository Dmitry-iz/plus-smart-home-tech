package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.deserializer.SensorEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final SnapshotAggregationService aggregationService;
    private final SnapshotMapperService snapshotMapperService;

    private static final String SENSORS_TOPIC = "telemetry.sensors.v1";
    private static final String SNAPSHOTS_TOPIC = "telemetry.snapshots.v1";
    private static final String CONSUMER_GROUP = "aggregator-group";

    // Флаг для graceful shutdown
    private volatile boolean running = true;
    private Consumer<String, SensorEventAvro> consumer;
    private Producer<String, byte[]> producer;

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start() {
        log.info("Starting Aggregation Service...");

        // Инициализация Kafka клиентов
        initializeKafkaClients();

        try {
            // Подписка на топик с событиями датчиков
            consumer.subscribe(List.of(SENSORS_TOPIC));
            log.info("Subscribed to topic: {}", SENSORS_TOPIC);
            log.info("Consumer group: {}", CONSUMER_GROUP);
            log.info("Will produce snapshots to topic: {}", SNAPSHOTS_TOPIC);

            // Основной цикл обработки
            while (running) {
                try {
                    // Опрос новых сообщений с таймаутом
                    ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(1000));

                    if (records.isEmpty()) {
                        continue; // Нет новых сообщений - продолжаем цикл
                    }

                    log.debug("Received {} records from Kafka", records.count());

                    // Обработка каждого полученного события
                    processRecords(records);

                    // Синхронная фиксация смещений после успешной обработки
                    consumer.commitSync();
                    log.debug("Committed offsets for {} processed records", records.count());

                } catch (WakeupException e) {
                    // WakeupException - нормальная ситуация при остановке
                    if (running) {
                        log.warn("WakeupException received but service is still running", e);
                    } else {
                        log.info("WakeupException received during shutdown");
                        break;
                    }
                } catch (Exception e) {
                    log.error("Unexpected error during record processing", e);
                    // Не прерываем цикл при ошибке обработки отдельных записей
                }
            }

        } catch (Exception e) {
            log.error("Critical error during sensor events processing", e);
        } finally {
            // Гарантированное освобождение ресурсов
            shutdown();
        }
    }

    /**
     * Останавливает сервис агрегации
     */
    public void stop() {
        log.info("Stopping Aggregation Service...");
        running = false;

        // Пробуждаем consumer.poll() для немедленного завершения
        if (consumer != null) {
            consumer.wakeup();
        }
    }

    /**
     * Инициализация Kafka consumer и producer
     */
    private void initializeKafkaClients() {
        // Настройка консьюмера
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                SensorEventDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100"); // Ограничение для батчинга

        // Настройка продюсера
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer",
                "org.apache.kafka.common.serialization.ByteArraySerializer");
        producerProps.put("acks", "all"); // Гарантия записи
        producerProps.put("retries", "3"); // Повторы при ошибках

        consumer = new KafkaConsumer<>(consumerProps);
        producer = new KafkaProducer<>(producerProps);

        log.info("Kafka clients initialized successfully");
    }

    /**
     * Обработка пачки записей из Kafka
     */
    private void processRecords(ConsumerRecords<String, SensorEventAvro> records) {
        int processedCount = 0;
        int snapshotSentCount = 0;

        for (ConsumerRecord<String, SensorEventAvro> record : records) {
            try {
                SensorEventAvro event = record.value();
                log.debug("Processing event from device: {}, hub: {}, partition: {}, offset: {}",
                        event.getId(), event.getHubId(), record.partition(), record.offset());

                // Обновляем состояние и получаем снапшот если он изменился
                Optional<SensorsSnapshotAvro> snapshotOpt = aggregationService.updateState(event);

                // Если снапшот изменился - отправляем в Kafka
                if (snapshotOpt.isPresent()) {
                    SensorsSnapshotAvro snapshot = snapshotOpt.get();
                    sendSnapshotToKafka(snapshot);
                    snapshotSentCount++;
                }

                processedCount++;

            } catch (Exception e) {
                log.error("Error processing record from topic: {}, partition: {}, offset: {}",
                        record.topic(), record.partition(), record.offset(), e);
                // Продолжаем обработку следующих записей несмотря на ошибку
            }
        }

        log.info("Processed {} events, sent {} snapshots", processedCount, snapshotSentCount);
    }

    /**
     * Отправка снапшота в Kafka
     */
    private void sendSnapshotToKafka(SensorsSnapshotAvro snapshot) {
        try {
            byte[] snapshotBytes = snapshotMapperService.snapshotToAvroBytes(snapshot);

            ProducerRecord<String, byte[]> snapshotRecord =
                    new ProducerRecord<>(SNAPSHOTS_TOPIC, snapshot.getHubId(), snapshotBytes);

            // Асинхронная отправка с callback для обработки результата
            producer.send(snapshotRecord, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Failed to send snapshot for hub: {} to topic: {}",
                            snapshot.getHubId(), SNAPSHOTS_TOPIC, exception);
                } else {
                    log.debug("Snapshot sent for hub: {} to topic: {}, partition: {}, offset: {}",
                            snapshot.getHubId(), metadata.topic(),
                            metadata.partition(), metadata.offset());
                }
            });

            log.info("Snapshot queued for hub: {} with {} devices",
                    snapshot.getHubId(), snapshot.getSensorsState().size());

        } catch (Exception e) {
            log.error("Failed to convert or send snapshot for hub: {}", snapshot.getHubId(), e);
        }
    }

    /**
     * Корректное завершение работы
     */
    private void shutdown() {
        log.info("Starting graceful shutdown...");

        try {
            // Шаг 1: Сбрасываем все данные продюсера из буфера
            if (producer != null) {
                producer.flush();
                log.info("Producer flush completed");
            }

            // Шаг 2: Фиксируем последние обработанные смещения
            if (consumer != null) {
                try {
                    consumer.commitSync();
                    log.info("Final offsets commit completed");
                } catch (Exception e) {
                    log.warn("Error during final commit", e);
                }
            }

        } catch (Exception e) {
            log.error("Error during shutdown phase", e);
        } finally {
            // Шаг 3: Закрываем ресурсы в правильном порядке
            try {
                if (consumer != null) {
                    consumer.close();
                    log.info("Consumer closed");
                }
            } catch (Exception e) {
                log.error("Error closing consumer", e);
            }

            try {
                if (producer != null) {
                    producer.close();
                    log.info("Producer closed");
                }
            } catch (Exception e) {
                log.error("Error closing producer", e);
            }

            log.info("Aggregation service stopped successfully");
        }
    }

    /**
     * Проверка статуса сервиса
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Получение статистики по снапшотам (для мониторинга)
     */
    public int getSnapshotCount() {
        return aggregationService.getSnapshotCount();
    }
}