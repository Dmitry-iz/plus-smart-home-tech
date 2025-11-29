package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("aggregator.kafka")
public class KafkaConfig {
    private String bootstrapServers = "localhost:9092";
    private String sensorsTopic = "telemetry.sensors.v1";
    private String snapshotsTopic = "telemetry.snapshots.v1";
    private String consumerGroup = "aggregator-group";
}