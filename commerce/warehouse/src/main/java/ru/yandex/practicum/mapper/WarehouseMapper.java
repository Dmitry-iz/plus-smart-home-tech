package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.entity.WarehouseAddress;

@Component
public class WarehouseMapper {

    public AddressDto toDto(WarehouseAddress address) {
        if (address == null) {
            // Возвращаем дефолтный адрес вместо null
            AddressDto dto = new AddressDto();
            dto.setCountry("ADDRESS_1");
            dto.setCity("ADDRESS_1");
            dto.setStreet("ADDRESS_1");
            dto.setHouse("ADDRESS_1");
            dto.setFlat("ADDRESS_1");
            return dto;
        }

        AddressDto dto = new AddressDto();
        dto.setCountry(address.getCountry());
        dto.setCity(address.getCity());
        dto.setStreet(address.getStreet());
        dto.setHouse(address.getHouse());
        dto.setFlat(address.getFlat()); // Теперь используем flat

        return dto;
    }
}