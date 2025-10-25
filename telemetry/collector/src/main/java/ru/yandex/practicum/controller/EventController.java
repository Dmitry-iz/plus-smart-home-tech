package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.model.HubEvent;
import ru.yandex.practicum.model.SensorEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.KafkaProducerService;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("Received sensor event: {}", event);

        try {
            kafkaProducerService.sendSensorEvent(event);
            log.debug("Sensor event sent to Kafka successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to process sensor event: {}", event, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.info("Received hub event: {}", event);

        try {
            kafkaProducerService.sendHubEvent(event);
            log.debug("Hub event sent to Kafka successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to process hub event: {}", event, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
