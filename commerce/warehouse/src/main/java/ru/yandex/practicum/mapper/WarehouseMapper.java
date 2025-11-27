package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.entity.WarehouseAddress;

@Component
public class WarehouseMapper {

    // УДАЛЯЕМ все методы с WarehouseItemDto - они больше не нужны

    // ОСТАВЛЯЕМ ТОЛЬКО метод для AddressDto
    public AddressDto toDto(WarehouseAddress address) {
        if (address == null) {
            return null;
        }

        AddressDto dto = new AddressDto();
        dto.setCountry(address.getCountry());
        dto.setCity(address.getCity());
        dto.setStreet(address.getStreet());
        dto.setHouse(address.getHouse());
        dto.setFlat(address.getApartment()); // Исправляем apartment на flat как в спецификации

        return dto;
    }
}