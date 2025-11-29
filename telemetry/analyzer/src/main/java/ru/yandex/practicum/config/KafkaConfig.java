package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("spring.kafka")
public class KafkaConfig {
    private String bootstrapServers = "localhost:9092";

    private Consumer consumer = new Consumer();
    private Producer producer = new Producer();

    @Getter
    @Setter
    public static class Consumer {
        private String groupId;
        private String autoOffsetReset = "earliest";
    }

    @Getter
    @Setter
    public static class Producer {
    }
}
