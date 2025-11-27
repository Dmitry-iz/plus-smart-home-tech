package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.WarehouseItemDto;
import ru.yandex.practicum.entity.WarehouseAddress;
import ru.yandex.practicum.entity.WarehouseItem;

@Component
public class WarehouseMapper {

    public WarehouseItemDto toDto(WarehouseItem item) {
        if (item == null) {
            return null;
        }

        WarehouseItemDto dto = new WarehouseItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());
        dto.setWidth(item.getWidth());
        dto.setHeight(item.getHeight());
        dto.setDepth(item.getDepth());
        dto.setWeight(item.getWeight());
        dto.setFragile(item.getFragile());

        return dto;
    }

    public WarehouseItem toEntity(WarehouseItemDto dto) {
        if (dto == null) {
            return null;
        }

        WarehouseItem item = new WarehouseItem();
        item.setId(dto.getId());
        item.setProductId(dto.getProductId());
        item.setQuantity(dto.getQuantity());
        item.setWidth(dto.getWidth());
        item.setHeight(dto.getHeight());
        item.setDepth(dto.getDepth());
        item.setWeight(dto.getWeight());
        item.setFragile(dto.getFragile());

        return item;
    }

    public AddressDto toDto(WarehouseAddress address) {
        if (address == null) {
            return null;
        }

        AddressDto dto = new AddressDto();
        dto.setCountry(address.getCountry());
        dto.setCity(address.getCity());
        dto.setStreet(address.getStreet());
        dto.setHouse(address.getHouse());
        dto.setApartment(address.getApartment());

        return dto;
    }
}