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

    default Payment toEntity(PaymentDto dto) {
        if (dto == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setPaymentId(dto.getPaymentId());
        payment.setTotalPayment(dto.getTotalPayment());
        payment.setDeliveryTotal(dto.getDeliveryTotal());
        payment.setFeeTotal(dto.getFeeTotal());
        payment.setStatus(ru.yandex.practicum.dto.payment.PaymentStatus.PENDING);

        return payment;
    }
}