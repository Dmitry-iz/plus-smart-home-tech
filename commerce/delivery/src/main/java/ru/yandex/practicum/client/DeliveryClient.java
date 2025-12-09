//package ru.yandex.practicum.client;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import ru.yandex.practicum.dto.delivery.DeliveryDto;
//import ru.yandex.practicum.dto.order.OrderDto;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//@FeignClient(name = "delivery")
//public interface DeliveryClient {
//
//    @PutMapping("/api/v1/delivery")
//    DeliveryDto planDelivery(@RequestBody DeliveryDto deliveryDto);
//
//    @PostMapping("/api/v1/delivery/successful")
//    void deliverySuccessful(@RequestBody UUID orderId);
//
//    @PostMapping("/api/v1/delivery/picked")
//    void deliveryPicked(@RequestBody UUID orderId);
//
//    @PostMapping("/api/v1/delivery/failed")
//    void deliveryFailed(@RequestBody UUID orderId);
//
//    @PostMapping("/api/v1/delivery/cost")
//    BigDecimal deliveryCost(@RequestBody OrderDto orderDto);
//}