package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.common.PageResponse;
import ru.yandex.practicum.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.dto.shoppingstore.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ProductService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ProductService productService;

    @Override
    @GetMapping
    public List<ProductDto> getProductsByCategory(
            @RequestParam("category") String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", required = false) List<String> sort) {

        Pageable pageable = createPageable(page, size, sort);
        Page<ProductDto> productsPage = productService.getProductsByCategory(category, pageable);

        return productsPage.getContent(); // Возвращаем только список
    }
//    @GetMapping
//    public ResponseEntity<PageResponse<ProductDto>> getProductsByCategory(
//            @RequestParam("category") String category,
//            @RequestParam(value = "page", defaultValue = "0") int page,
//            @RequestParam(value = "size", defaultValue = "20") int size,
//            @RequestParam(value = "sort", required = false) List<String> sort) {
//
//        Pageable pageable = createPageable(page, size, sort);
//        Page<ProductDto> productsPage = productService.getProductsByCategory(category, pageable);
//
//        PageResponse<ProductDto> response = new PageResponse<>(
//                productsPage.getContent(),
//                productsPage.getNumber(),
//                productsPage.getSize(),
//                productsPage.getTotalElements(),
//                productsPage.getTotalPages(),
//                productsPage.isLast()
//        );
//
//        return ResponseEntity.ok(response);
//    }

    @Override
    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable("productId") UUID productId) {
        return productService.getProduct(productId);
    }

    @Override
    @PutMapping
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @Override
    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return productService.updateProduct(productDto);
    }

    @Override
    @PostMapping("/removeProductFromStore")
    public Boolean removeProductFromStore(@RequestBody UUID productId) {
        return productService.removeProductFromStore(productId);
    }

    // РЕАЛИЗАЦИЯ ДЛЯ FEIGN CLIENT - только JSON
    @Override
    @PostMapping("/quantityState")
    public Boolean setQuantityState(@RequestBody SetProductQuantityStateRequest request) {
        return productService.setQuantityState(request);
    }

    // ОТДЕЛЬНЫЙ ENDPOINT ДЛЯ ТЕСТОВ С ПАРАМЕТРАМИ URL
    @PostMapping(value = "/quantityState", params = {"productId", "quantityState"})
    public Boolean setQuantityStateFromUrl(
            @RequestParam("productId") UUID productId,
            @RequestParam("quantityState") String quantityState) {

        log.info("Setting quantity state from URL params - productId: {}, quantityState: {}",
                productId, quantityState);

        SetProductQuantityStateRequest request = new SetProductQuantityStateRequest();
        request.setProductId(productId);
        request.setQuantityState(ru.yandex.practicum.dto.shoppingstore.QuantityState.valueOf(quantityState));

        return productService.setQuantityState(request);
    }

    private Pageable createPageable(int page, int size, List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            return PageRequest.of(page, size);
        }

        Sort.Direction direction = Sort.Direction.ASC;
        String sortField = "productName";

        if (sort.size() > 0) {
            String sortParam = sort.get(0);
            if (sortParam.contains(",")) {
                String[] parts = sortParam.split(",");
                sortField = parts[0];
                direction = Sort.Direction.fromString(parts[1]);
            } else {
                sortField = sortParam;
            }
        }

        return PageRequest.of(page, size, direction, sortField);
    }
}