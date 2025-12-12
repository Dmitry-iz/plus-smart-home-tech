package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.entity.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "paymentId", source = "paymentId")
    @Mapping(target = "totalPayment", source = "totalPayment")
    @Mapping(target = "deliveryTotal", source = "deliveryTotal")
    @Mapping(target = "feeTotal", source = "feeTotal")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "paymentId", source = "paymentId")
    @Mapping(target = "totalPayment", source = "totalPayment")
    @Mapping(target = "deliveryTotal", source = "deliveryTotal")
    @Mapping(target = "feeTotal", source = "feeTotal")
    @Mapping(target = "status", ignore = true)
    Payment toEntity(PaymentDto dto);
}