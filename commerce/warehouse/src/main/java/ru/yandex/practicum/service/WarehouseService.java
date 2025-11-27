package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.entity.WarehouseAddress;
import ru.yandex.practicum.entity.WarehouseItem;
import ru.yandex.practicum.exception.WarehouseItemNotFoundException;
import ru.yandex.practicum.exception.InsufficientQuantityException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.repository.WarehouseAddressRepository;
import ru.yandex.practicum.repository.WarehouseItemRepository;

import java.util.HashMap;
import java.util.List;
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

    public AddressDto getWarehouseAddress() {
        log.info("Getting warehouse address");

        // Получаем существующий адрес или создаем случайный
        List<WarehouseAddress> addresses = warehouseAddressRepository.findAll();
        WarehouseAddress address;

        if (addresses.isEmpty()) {
            address = WarehouseAddress.getRandomAddress();
            address = warehouseAddressRepository.save(address);
        } else {
            address = addresses.get(0); // Берем первый адрес
        }

        return warehouseMapper.toDto(address);
    }

    public CartValidationResponse checkCartAvailability(CartValidationRequest request) {
        log.info("Checking cart availability: {}", request);

        Map<UUID, String> validationErrors = new HashMap<>();
        boolean isValid = true;

        for (Map.Entry<UUID, Integer> entry : request.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Integer requestedQuantity = entry.getValue();

            WarehouseItem item = warehouseItemRepository.findByProductId(productId)
                    .orElse(null);

            if (item == null) {
                validationErrors.put(productId, "Product not found in warehouse");
                isValid = false;
            } else if (item.getQuantity() < requestedQuantity) {
                validationErrors.put(productId,
                        "Insufficient quantity. Available: " + item.getQuantity() + ", Requested: " + requestedQuantity);
                isValid = false;
            }
        }

        return new CartValidationResponse(isValid, validationErrors);
    }

    @Transactional
    public WarehouseItemDto addWarehouseItem(WarehouseItemDto itemDto) {
        log.info("Adding warehouse item: {}", itemDto.getProductId());

        // Проверяем, существует ли уже товар
        warehouseItemRepository.findByProductId(itemDto.getProductId())
                .ifPresent(existingItem -> {
                    throw new IllegalArgumentException("Warehouse item already exists for product: " + itemDto.getProductId());
                });

        WarehouseItem item = warehouseMapper.toEntity(itemDto);
        if (item.getId() == null) {
            item.setId(UUID.randomUUID());
        }

        WarehouseItem savedItem = warehouseItemRepository.save(item);
        return warehouseMapper.toDto(savedItem);
    }

    @Transactional
    public WarehouseItemDto updateItemQuantity(UUID productId, Integer quantity) {
        log.info("Updating quantity for product: {}, quantity: {}", productId, quantity);

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        WarehouseItem item = warehouseItemRepository.findByProductId(productId)
                .orElseThrow(() -> new WarehouseItemNotFoundException(productId));

        item.setQuantity(quantity);
        WarehouseItem updatedItem = warehouseItemRepository.save(item);

        return warehouseMapper.toDto(updatedItem);
    }

    @Transactional
    public void reserveItems(Map<UUID, Integer> products) {
        log.info("Reserving items: {}", products);

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            WarehouseItem item = warehouseItemRepository.findByProductId(productId)
                    .orElseThrow(() -> new WarehouseItemNotFoundException(productId));

            if (item.getQuantity() < quantity) {
                throw new InsufficientQuantityException(productId, quantity, item.getQuantity());
            }

            // Резервируем товар (уменьшаем доступное количество)
            item.setQuantity(item.getQuantity() - quantity);
            warehouseItemRepository.save(item);
        }
    }

    @Transactional
    public void releaseItems(Map<UUID, Integer> products) {
        log.info("Releasing items: {}", products);

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            WarehouseItem item = warehouseItemRepository.findByProductId(productId)
                    .orElseThrow(() -> new WarehouseItemNotFoundException(productId));

            // Возвращаем товар (увеличиваем доступное количество)
            item.setQuantity(item.getQuantity() + quantity);
            warehouseItemRepository.save(item);
        }
    }

    public WarehouseItemDto getWarehouseItem(UUID productId) {
        log.info("Getting warehouse item for product: {}", productId);

        WarehouseItem item = warehouseItemRepository.findByProductId(productId)
                .orElseThrow(() -> new WarehouseItemNotFoundException(productId));

        return warehouseMapper.toDto(item);
    }
}