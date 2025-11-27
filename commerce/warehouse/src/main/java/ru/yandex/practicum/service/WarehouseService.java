package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.entity.WarehouseAddress;
import ru.yandex.practicum.entity.WarehouseItem;
import ru.yandex.practicum.exception.WarehouseItemNotFoundException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.repository.WarehouseAddressRepository;
import ru.yandex.practicum.repository.WarehouseItemRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseService {

    private final WarehouseItemRepository warehouseItemRepository;
    private final WarehouseAddressRepository warehouseAddressRepository;
    private final WarehouseMapper warehouseMapper;

    // 1. Добавить новый товар на склад
    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest request) {
        log.info("Adding new product to warehouse: {}", request.getProductId());

        // Проверяем, существует ли уже товар
        if (warehouseItemRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new IllegalArgumentException("Product already exists in warehouse: " + request.getProductId());
        }

        // Создаем новый товар на складе
        WarehouseItem item = new WarehouseItem();
        item.setProductId(request.getProductId());
        item.setQuantity(0); // Начальное количество 0

        // Заполняем характеристики из dimension
        if (request.getDimension() != null) {
            item.setWidth(request.getDimension().getWidth());
            item.setHeight(request.getDimension().getHeight());
            item.setDepth(request.getDimension().getDepth());
        }

        item.setWeight(request.getWeight());
        item.setFragile(request.getFragile() != null ? request.getFragile() : false);

        warehouseItemRepository.save(item);
        log.info("New product added to warehouse: {}", request.getProductId());
    }

    // 2. Проверить доступность товаров для корзины
    public BookedProductsDto checkProductAvailability(ShoppingCartDto shoppingCart) {
        log.info("Checking availability for shopping cart: {}", shoppingCart.getShoppingCartId());

        Double totalVolume = 0.0;
        Double totalWeight = 0.0;
        Boolean hasFragile = false;

        // Проверяем каждый товар в корзине
        for (Map.Entry<UUID, Integer> entry : shoppingCart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Integer requestedQuantity = entry.getValue();

            WarehouseItem item = warehouseItemRepository.findByProductId(productId)
                    .orElseThrow(() -> new WarehouseItemNotFoundException(productId));

            // Проверяем достаточно ли товара
            if (item.getQuantity() < requestedQuantity) {
                throw new IllegalArgumentException("Insufficient quantity for product: " + productId);
            }

            // Рассчитываем объем и вес
            if (item.getWidth() != null && item.getHeight() != null && item.getDepth() != null) {
                Double volume = item.getWidth().doubleValue() * item.getHeight().doubleValue() * item.getDepth().doubleValue();
                totalVolume += volume * requestedQuantity;
            }

            if (item.getWeight() != null) {
                totalWeight += item.getWeight().doubleValue() * requestedQuantity;
            }

            if (item.getFragile() != null && item.getFragile()) {
                hasFragile = true;
            }
        }

        return new BookedProductsDto(totalVolume, totalWeight, hasFragile);
    }

    // 3. Добавить количество существующего товара
    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        log.info("Adding quantity to product: {}, quantity: {}", request.getProductId(), request.getQuantity());

        WarehouseItem item = warehouseItemRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new WarehouseItemNotFoundException(request.getProductId()));

        // Увеличиваем количество
        item.setQuantity(item.getQuantity() + request.getQuantity().intValue());
        warehouseItemRepository.save(item);

        log.info("Quantity updated for product: {}, new quantity: {}", request.getProductId(), item.getQuantity());
    }

    // 4. Получить адрес склада
    public AddressDto getWarehouseAddress() {
        log.info("Getting warehouse address");

        // Получаем существующий адрес или создаем случайный
        WarehouseAddress address = warehouseAddressRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    WarehouseAddress newAddress = WarehouseAddress.getRandomAddress();
                    return warehouseAddressRepository.save(newAddress);
                });

        return warehouseMapper.toDto(address);
    }
}