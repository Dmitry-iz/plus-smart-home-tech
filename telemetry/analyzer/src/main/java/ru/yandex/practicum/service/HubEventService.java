package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final DecoderFactory decoderFactory = DecoderFactory.get();

    public void processHubEvent(byte[] eventData) {
        try {
            HubEventAvro hubEvent = deserializeHubEvent(eventData);
            // Здесь будет логика обработки событий хаба
            // (добавление/удаление устройств и сценариев)
            log.info("Processed hub event for hub: {}", hubEvent.getHubId());
        } catch (Exception e) {
            log.error("Failed to process hub event", e);
        }
    }

    private HubEventAvro deserializeHubEvent(byte[] data) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            BinaryDecoder decoder = decoderFactory.binaryDecoder(inputStream, null);
            SpecificDatumReader<HubEventAvro> reader = new SpecificDatumReader<>(HubEventAvro.getClassSchema());
            return reader.read(null, decoder);
        }
    }
}
