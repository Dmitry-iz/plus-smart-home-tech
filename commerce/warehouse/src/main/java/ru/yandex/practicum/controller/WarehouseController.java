package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.service.WarehouseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    // 1. Добавить новый товар на склад
    @PutMapping
    public ResponseEntity<Void> newProductInWarehouse(@RequestBody NewProductInWarehouseRequest request) {
        log.info("Adding new product to warehouse: {}", request.getProductId());
        warehouseService.addNewProduct(request);
        return ResponseEntity.ok().build();
    }

    // 2. Проверить доступность товаров для корзины
    @PostMapping("/check")
    public ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(@RequestBody ShoppingCartDto shoppingCart) {
        log.info("Checking product availability for shopping cart: {}", shoppingCart.getShoppingCartId());
        BookedProductsDto result = warehouseService.checkProductAvailability(shoppingCart);
        return ResponseEntity.ok(result);
    }

    // 3. Добавить количество существующего товара
    @PostMapping("/add")
    public ResponseEntity<Void> addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request) {
        log.info("Adding quantity to existing product: {}, quantity: {}", request.getProductId(), request.getQuantity());
        warehouseService.addProductQuantity(request);
        return ResponseEntity.ok().build();
    }

    // 4. Получить адрес склада
    @GetMapping("/address")
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        log.info("Getting warehouse address");
        AddressDto address = warehouseService.getWarehouseAddress();
        return ResponseEntity.ok(address);
    }
}