package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.WarehouseAddress;

public interface WarehouseAddressRepository extends JpaRepository<WarehouseAddress, Long> {
    // Будет только одна запись с адресом склада
}