//package ru.yandex.practicum.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import ru.yandex.practicum.dto.common.PageableDto;
//import ru.yandex.practicum.dto.shoppingstore.ProductDto;
//import ru.yandex.practicum.dto.shoppingstore.QuantityState;
//import ru.yandex.practicum.dto.shoppingstore.SetProductQuantityStateRequest;
//import ru.yandex.practicum.entity.Product;
//import ru.yandex.practicum.exception.ProductNotFoundException;
//import ru.yandex.practicum.mapper.ProductMapper;
//import ru.yandex.practicum.repository.ProductRepository;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class ProductService {
//
//    private final ProductRepository productRepository;
//    private final ProductMapper productMapper;
//
//    public List<ProductDto> getProductsByCategory(String category, PageableDto pageable) {
//        log.info("Getting products by category: {}", category);
//
//        // TODO: реализовать пагинацию когда добавим Pageable
//        var productCategory = ru.yandex.practicum.dto.shoppingstore.ProductCategory.valueOf(category);
//        var products = productRepository.findActiveProductsByCategory(productCategory);
//
//        return products.stream()
//                .map(productMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    public ProductDto getProduct(UUID productId) {
//        log.info("Getting product by id: {}", productId);
//
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ProductNotFoundException(productId));
//
//        return productMapper.toDto(product);
//    }
//
//    @Transactional
//    public ProductDto createProduct(ProductDto productDto) {
//        log.info("Creating product: {}", productDto.getProductName());
//
//        Product product = productMapper.toEntity(productDto);
//        if (product.getProductId() == null) {
//            product.setProductId(UUID.randomUUID());
//        }
//
//        Product savedProduct = productRepository.save(product);
//        return productMapper.toDto(savedProduct);
//    }
//
//    @Transactional
//    public ProductDto updateProduct(ProductDto productDto) {
//        log.info("Updating product: {}", productDto.getProductId());
//
//        Product existingProduct = productRepository.findById(productDto.getProductId())
//                .orElseThrow(() -> new ProductNotFoundException(productDto.getProductId()));
//
//        productMapper.updateEntityFromDto(productDto, existingProduct);
//        Product updatedProduct = productRepository.save(existingProduct);
//
//        return productMapper.toDto(updatedProduct);
//    }
//
//    @Transactional
//    public Boolean removeProductFromStore(UUID productId) {
//        log.info("Removing product from store: {}", productId);
//
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ProductNotFoundException(productId));
//
//        product.setProductState(ru.yandex.practicum.dto.shoppingstore.ProductState.DEACTIVATE);
//        productRepository.save(product);
//
//        return true;
//    }
//
//    @Transactional
//    public Boolean setQuantityState(SetProductQuantityStateRequest request) {
//        log.info("Setting quantity state for product: {}", request.getProductId());
//
//        Product product = productRepository.findById(request.getProductId())
//                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));
//
//        product.setQuantityState(request.getQuantityState());
//        productRepository.save(product);
//
//        return true;
//    }
//}

package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.dto.shoppingstore.SetProductQuantityStateRequest;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // ИСПРАВЛЯЕМ ТОЛЬКО ЭТОТ МЕТОД - добавляем пагинацию
    public Page<ProductDto> getProductsByCategory(String category, Pageable pageable) {
        log.info("Getting products by category: {}, pageable: {}", category, pageable);

        var productCategory = ru.yandex.practicum.dto.shoppingstore.ProductCategory.valueOf(category);
        Page<Product> products = productRepository.findByProductCategoryAndProductState(
                productCategory,
                ru.yandex.practicum.dto.shoppingstore.ProductState.ACTIVE,
                pageable
        );

        return products.map(productMapper::toDto);
    }

    // ОСТАЛЬНЫЕ МЕТОДЫ НЕ МЕНЯЕМ - они работают правильно
    public ProductDto getProduct(UUID productId) {
        log.info("Getting product by id: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        return productMapper.toDto(product);
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Creating product: {}", productDto.getProductName());

        Product product = productMapper.toEntity(productDto);
        if (product.getProductId() == null) {
            product.setProductId(UUID.randomUUID());
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("Updating product: {}", productDto.getProductId());

        Product existingProduct = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(productDto.getProductId()));

        productMapper.updateEntityFromDto(productDto, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);

        return productMapper.toDto(updatedProduct);
    }

    @Transactional
    public Boolean removeProductFromStore(UUID productId) {
        log.info("Removing product from store: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setProductState(ru.yandex.practicum.dto.shoppingstore.ProductState.DEACTIVATE);
        productRepository.save(product);

        return true;
    }

    @Transactional
    public Boolean setQuantityState(SetProductQuantityStateRequest request) {
        log.info("Setting quantity state for product: {}", request.getProductId());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);

        return true;
    }
}