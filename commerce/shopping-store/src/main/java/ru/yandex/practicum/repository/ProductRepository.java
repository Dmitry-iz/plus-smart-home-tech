package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.dto.shoppingstore.ProductCategory;
import ru.yandex.practicum.dto.shoppingstore.ProductState;

import java.util.List;
import java.util.UUID;

//public interface ProductRepository extends JpaRepository<Product, UUID> {
//
//    Page<Product> findByProductCategoryAndProductState(
//            ProductCategory productCategory,
//            ProductState productState,
//            Pageable pageable
//    );
//
//    List<Product> findByProductState(ProductState productState);
//
//    @Query("SELECT p FROM Product p WHERE p.productCategory = :category AND p.productState = 'ACTIVE'")
//    List<Product> findActiveProductsByCategory(@Param("category") ProductCategory category);
//
//    boolean existsByProductIdAndProductState(UUID productId, ProductState productState);
//}

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByProductCategoryAndProductState(
            ru.yandex.practicum.dto.shoppingstore.ProductCategory productCategory,  // DTO enum
            ru.yandex.practicum.dto.shoppingstore.ProductState productState,        // DTO enum
            Pageable pageable
    );

    List<Product> findByProductState(ru.yandex.practicum.dto.shoppingstore.ProductState productState);

    @Query("SELECT p FROM Product p WHERE p.productCategory = :category AND p.productState = 'ACTIVE'")
    List<Product> findActiveProductsByCategory(@Param("category") ru.yandex.practicum.dto.shoppingstore.ProductCategory category);

    boolean existsByProductIdAndProductState(UUID productId, ru.yandex.practicum.dto.shoppingstore.ProductState productState);
}