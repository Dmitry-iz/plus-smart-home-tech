package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.UUID;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/api/v1/warehouse/check-availability")
    CartValidationResponse checkCartAvailability(@RequestBody CartValidationRequest request);

    // Методы для администрации (добавление/обновление товаров на складе)
    @PutMapping("/api/v1/warehouse/items")
    WarehouseItemDto addWarehouseItem(@RequestBody WarehouseItemDto itemDto);

    @PostMapping("/api/v1/warehouse/items/{productId}/quantity")
    WarehouseItemDto updateItemQuantity(
            @PathVariable("productId") UUID productId,
            @RequestParam("quantity") Integer quantity
    );
}