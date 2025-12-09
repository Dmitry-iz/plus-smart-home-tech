//package ru.yandex.practicum.client;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import ru.yandex.practicum.config.FeignConfig;
//import ru.yandex.practicum.dto.warehouse.AddressDto;
//import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
//import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
//
//@FeignClient(name = "warehouse", url = "${feign.client.warehouse.url}", configuration = FeignConfig.class)
//public interface WarehouseServiceClient {
//
//    @PostMapping("/api/v1/warehouse/assembly")
//    BookedProductsDto assemblyProductForOrderFromShoppingCart(
//            @RequestBody AssemblyProductsForOrderRequest request);
//
//    @GetMapping("/api/v1/warehouse/address")
//    AddressDto getWarehouseAddress();
//}