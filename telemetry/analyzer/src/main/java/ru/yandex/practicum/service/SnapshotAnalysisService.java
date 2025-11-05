package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotAnalysisService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioExecutionService scenarioExecutionService;
    private final DecoderFactory decoderFactory = DecoderFactory.get();

    public void analyzeSnapshot(byte[] snapshotData) {
        try {
            SensorsSnapshotAvro snapshot = deserializeSnapshot(snapshotData);
            String hubId = snapshot.getHubId();

            // Загружаем сценарии для данного хаба
            var scenarios = scenarioRepository.findByHubId(hubId);

            if (scenarios.isEmpty()) {
                log.debug("No scenarios found for hub: {}", hubId);
                return;
            }

            log.info("Analyzing snapshot for hub: {} with {} scenarios",
                    hubId, scenarios.size());

            // Проверяем условия сценариев и выполняем действия
            scenarioExecutionService.executeScenarios(snapshot, scenarios);

        } catch (Exception e) {
            log.error("Failed to analyze snapshot", e);
        }
    }

    private SensorsSnapshotAvro deserializeSnapshot(byte[] data) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            BinaryDecoder decoder = decoderFactory.binaryDecoder(inputStream, null);
            SpecificDatumReader<SensorsSnapshotAvro> reader =
                    new SpecificDatumReader<>(SensorsSnapshotAvro.getClassSchema());
            return reader.read(null, decoder);
        }
    }
}
