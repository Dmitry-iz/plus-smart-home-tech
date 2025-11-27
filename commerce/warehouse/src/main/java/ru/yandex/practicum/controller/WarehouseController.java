package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.service.WarehouseService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseClient {

    private final WarehouseService warehouseService;

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }

    @Override
    @PostMapping("/check-availability")
    public CartValidationResponse checkCartAvailability(@RequestBody CartValidationRequest request) {
        return warehouseService.checkCartAvailability(request);
    }

    @Override
    @PutMapping("/items")
    public WarehouseItemDto addWarehouseItem(@RequestBody WarehouseItemDto itemDto) {
        return warehouseService.addWarehouseItem(itemDto);
    }

    @Override
    @PostMapping("/items/{productId}/quantity")
    public WarehouseItemDto updateItemQuantity(
            @PathVariable("productId") UUID productId,
            @RequestParam("quantity") Integer quantity) {
        return warehouseService.updateItemQuantity(productId, quantity);
    }
}