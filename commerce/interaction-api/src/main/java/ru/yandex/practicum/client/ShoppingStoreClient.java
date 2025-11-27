package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.common.PageResponse;
import ru.yandex.practicum.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.dto.shoppingstore.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

//    @GetMapping("/api/v1/shopping-store")
//    ResponseEntity<PageResponse<ProductDto>> getProductsByCategory(
//            @RequestParam("category") String category,
//            @RequestParam(value = "page", defaultValue = "0") int page,
//            @RequestParam(value = "size", defaultValue = "20") int size,
//            @RequestParam(value = "sort", required = false) List<String> sort
//    );

    @GetMapping("/api/v1/shopping-store")
    List<ProductDto> getProductsByCategory(  // Меняем возвращаемый тип
                                             @RequestParam("category") String category,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "20") int size,
                                             @RequestParam(value = "sort", required = false) List<String> sort
    );

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProduct(@PathVariable("productId") UUID productId);

    @PutMapping("/api/v1/shopping-store")
    ProductDto createProduct(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    Boolean removeProductFromStore(@RequestBody UUID productId);

    // ОРИГИНАЛЬНЫЙ МЕТОД - ТОЛЬКО ДЛЯ JSON
    @PostMapping("/api/v1/shopping-store/quantityState")
    Boolean setQuantityState(@RequestBody SetProductQuantityStateRequest request);
}