package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.entity.Delivery;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "fromAddress", expression = "java(mapToAddressDto(delivery, true))")
    @Mapping(target = "toAddress", expression = "java(mapToAddressDto(delivery, false))")
    DeliveryDto toDto(Delivery delivery);

    @Mapping(target = "fromCountry", source = "fromAddress.country")
    @Mapping(target = "fromCity", source = "fromAddress.city")
    @Mapping(target = "fromStreet", source = "fromAddress.street")
    @Mapping(target = "fromHouse", source = "fromAddress.house")
    @Mapping(target = "fromFlat", source = "fromAddress.flat")
    @Mapping(target = "toCountry", source = "toAddress.country")
    @Mapping(target = "toCity", source = "toAddress.city")
    @Mapping(target = "toStreet", source = "toAddress.street")
    @Mapping(target = "toHouse", source = "toAddress.house")
    @Mapping(target = "toFlat", source = "toAddress.flat")
    Delivery toEntity(DeliveryDto dto);

    default AddressDto mapToAddressDto(Delivery delivery, boolean isFrom) {
        if (delivery == null) {
            return null;
        }

        AddressDto addressDto = new AddressDto();
        if (isFrom) {
            addressDto.setCountry(delivery.getFromCountry());
            addressDto.setCity(delivery.getFromCity());
            addressDto.setStreet(delivery.getFromStreet());
            addressDto.setHouse(delivery.getFromHouse());
            addressDto.setFlat(delivery.getFromFlat());
        } else {
            addressDto.setCountry(delivery.getToCountry());
            addressDto.setCity(delivery.getToCity());
            addressDto.setStreet(delivery.getToStreet());
            addressDto.setHouse(delivery.getToHouse());
            addressDto.setFlat(delivery.getToFlat());
        }
        return addressDto;
    }
}