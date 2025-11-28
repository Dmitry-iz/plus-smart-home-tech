package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.common.PageResponse;
import ru.yandex.practicum.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.dto.shoppingstore.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ProductService productService;

    @Override
    @GetMapping
    public PageResponse<ProductDto> getProductsByCategory(
            @RequestParam("category") String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", required = false) List<String> sort) {

        Pageable pageable = createPageable(page, size, sort);
        Page<ProductDto> productsPage = productService.getProductsByCategory(category, pageable);

        // Преобразуем Spring Page в наш PageResponse
        List<PageResponse.SortInfo> sortInfo = convertSort(pageable.getSort());

        return new PageResponse<>(
                productsPage.getContent(),
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalElements(),
                productsPage.getTotalPages(),
                productsPage.isLast(),
                sortInfo
        );
    }

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

    // Основной метод для Feign (JSON)
    @Override
    @PostMapping("/quantityState")
    public Boolean setQuantityState(@RequestBody SetProductQuantityStateRequest request) {
        return productService.setQuantityState(request);
    }

    // Дополнительный метод для тестов (URL параметры)
    @PostMapping(value = "/quantityState", params = {"productId", "quantityState"})
    public Boolean setQuantityStateFromParams(
            @RequestParam("productId") UUID productId,
            @RequestParam("quantityState") String quantityState) {

        SetProductQuantityStateRequest request = new SetProductQuantityStateRequest();
        request.setProductId(productId);
        request.setQuantityState(ru.yandex.practicum.dto.shoppingstore.QuantityState.valueOf(quantityState));

        return productService.setQuantityState(request);
    }

//    private Pageable createPageable(int page, int size, List<String> sort) {
//        if (sort == null || sort.isEmpty()) {
//            return PageRequest.of(page, size);
//        }
//
//        // Исправляем обработку сортировки - правильно парсим "field,DIRECTION"
//        List<Sort.Order> orders = new ArrayList<>();
//        for (String sortParam : sort) {
//            if (sortParam.contains(",")) {
//                String[] parts = sortParam.split(",");
//                String field = parts[0].trim();
//                Sort.Direction direction = parts.length > 1 ?
//                        Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.ASC;
//                orders.add(new Sort.Order(direction, field));
//            } else {
//                orders.add(new Sort.Order(Sort.Direction.ASC, sortParam.trim()));
//            }
//        }
//
//        return PageRequest.of(page, size, Sort.by(orders));
//    }

    private Pageable createPageable(int page, int size, List<String> sort) {
        log.info("Creating pageable - page: {}, size: {}, sort: {}", page, size, sort);

        if (sort == null || sort.isEmpty()) {
            return PageRequest.of(page, size);
        }

        // Простая обработка для отладки
        for (String sortParam : sort) {
            log.info("Processing sort param: {}", sortParam);
            try {
                String[] parts = sortParam.split(",");
                String field = parts[0].trim();
                Sort.Direction direction = parts.length > 1 ?
                        Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.ASC;

                log.info("Parsed - field: {}, direction: {}", field, direction);
                return PageRequest.of(page, size, direction, field);

            } catch (Exception e) {
                log.error("Error parsing sort: {}", sortParam, e);
            }
        }

        return PageRequest.of(page, size);
    }


    private List<PageResponse.SortInfo> convertSort(Sort sort) {
        return sort.stream()
                .map(order -> new PageResponse.SortInfo(order.getProperty(), order.getDirection().name()))
                .collect(Collectors.toList());
    }
}