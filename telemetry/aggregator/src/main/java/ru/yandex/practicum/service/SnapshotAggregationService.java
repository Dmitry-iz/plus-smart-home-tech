package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SnapshotAggregationService {

    // Хранилище снапшотов по hubId
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        String deviceId = event.getId();

        log.debug("Updating state for hub: {}, device: {}", hubId, deviceId);

        // Получаем или создаем снапшот для хаба
        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, this::createEmptySnapshot);

        // Проверяем, нужно ли обновлять данные
        if (!shouldUpdate(snapshot, deviceId, event)) {
            log.debug("No update needed for device: {}", deviceId);
            return Optional.empty();
        }

        // Создаем новое состояние устройства
        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        // Создаем новую Map с правильным типом - Map<String, SensorStateAvro>
        Map<String, SensorStateAvro> newStateMap = new HashMap<>(snapshot.getSensorsState());
        newStateMap.put(deviceId, newState);

        // Создаем обновленный снапшот
        SensorsSnapshotAvro updatedSnapshot = SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(event.getTimestamp())
                .setSensorsState(newStateMap)
                .build();

        // Сохраняем обновленный снапшот
        snapshots.put(hubId, updatedSnapshot);

        log.info("Snapshot updated for hub: {}, device: {}", hubId, deviceId);
        return Optional.of(updatedSnapshot);
    }

    private boolean shouldUpdate(SensorsSnapshotAvro snapshot, String deviceId, SensorEventAvro event) {
        // Если устройства еще нет в снапшоте - обязательно обновляем
        if (!snapshot.getSensorsState().containsKey(deviceId)) {
            log.debug("New device detected: {}", deviceId);
            return true;
        }

        SensorStateAvro oldState = snapshot.getSensorsState().get(deviceId);

        // События с одинаковой или более старой временной меткой игнорируем
        if (oldState.getTimestamp() > event.getTimestamp()) {
            log.debug("Ignoring older event for device: {}. Current: {}, Event: {}",
                    deviceId, oldState.getTimestamp(), event.getTimestamp());
            return false;
        }

        // Если временная метка совпадает, проверяем данные
        if (oldState.getTimestamp() == event.getTimestamp()) {
            if (dataEquals(oldState.getData(), event.getPayload())) {
                log.debug("Duplicate event with same timestamp and data for device: {}", deviceId);
                return false;
            }
            log.debug("Event with same timestamp but different data for device: {}", deviceId);
        }

        // Если данные не изменились, но временная метка новее - все равно обновляем временную метку
        if (dataEquals(oldState.getData(), event.getPayload())) {
            log.debug("Data unchanged but updating timestamp for device: {}", deviceId);
            return true;
        }

        log.debug("Data changed for device: {}", deviceId);
        return true;
    }

    private SensorsSnapshotAvro createEmptySnapshot(String hubId) {
        return SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(0L)
                .setSensorsState(new HashMap<>())
                .build();
    }

    private boolean dataEquals(Object oldData, Object newData) {
        if (oldData == null && newData == null) return true;
        if (oldData == null || newData == null) return false;
        return oldData.equals(newData);
    }

    // Метод для получения текущего снапшота (может пригодиться для тестов)
    public SensorsSnapshotAvro getSnapshot(String hubId) {
        return snapshots.get(hubId);
    }

    // Метод для отладки - получение количества снапшотов
    public int getSnapshotCount() {
        return snapshots.size();
    }

    // Метод для очистки снапшотов (может пригодиться для тестов)
    public void clearSnapshots() {
        snapshots.clear();
        log.info("All snapshots cleared");
    }
}