//package ru.yandex.practicum.client;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import ru.yandex.practicum.config.FeignConfig;
//import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
//
//@FeignClient(name = "warehouse", url = "${feign.client.warehouse.url}", configuration = FeignConfig.class)
//public interface WarehouseDeliveryClient {
//
//    @PostMapping("/api/v1/warehouse/shipped")
//    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request);
//}

package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.config.FeignConfig;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;

@FeignClient(name = "warehouse", contextId = "deliveryWarehouseClient", url = "${feign.client.warehouse.url}")
public interface WarehouseDeliveryClient {

    @PostMapping("/api/v1/warehouse/shipped")
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request);
}