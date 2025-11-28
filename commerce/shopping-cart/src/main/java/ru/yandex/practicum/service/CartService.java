package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.entity.Cart;
import ru.yandex.practicum.entity.CartItem;
import ru.yandex.practicum.exception.CartNotFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.repository.CartRepository;
import ru.yandex.practicum.repository.CartItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    public ShoppingCartDto getShoppingCart(String username) {
        validateUsername(username);
        log.info("Getting shopping cart for user: {}", username);

        Cart cart = cartRepository.findActiveCartWithItems(username)
                .orElseGet(() -> createNewCart(username));

        return shoppingCartMapper.toDto(cart);
    }



//    @Transactional
//    public ShoppingCartDto addProductsToCart(String username, Map<UUID, Integer> products) {
//        validateUsername(username);
//        log.info("Adding products to cart for user: {}, products: {}", username, products);
//
//        Cart cart = cartRepository.findActiveCartWithItems(username)
//                .orElseGet(() -> createNewCart(username));
//
//        // КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ: Postman отправляет Map<UUID, Integer>, где значение - это КОЛИЧЕСТВО, а не приращение
//        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
//            UUID productId = entry.getKey();
//            Integer targetQuantity = entry.getValue(); // Это целевое количество, а не приращение!
//
//            cartItemRepository.findByCartShoppingCartIdAndProductId(cart.getShoppingCartId(), productId)
//                    .ifPresentOrElse(
//                            cartItem -> {
//                                // Если товар уже есть - УСТАНАВЛИВАЕМ указанное количество
//                                cartItem.setQuantity(targetQuantity);
//                                cartItemRepository.save(cartItem);
//                            },
//                            () -> {
//                                // Если товара нет - добавляем с указанным количеством
//                                CartItem newItem = new CartItem();
//                                newItem.setCart(cart);
//                                newItem.setProductId(productId);
//                                newItem.setQuantity(targetQuantity);
//                                cartItemRepository.save(newItem);
//                            }
//                    );
//        }
//
//        // Получаем обновленную корзину
//        Cart updatedCart = cartRepository.findActiveCartWithItems(username)
//                .orElseThrow(() -> new CartNotFoundException(username, cart.getShoppingCartId()));
//
//        return shoppingCartMapper.toDto(updatedCart);
//    }

//    @Transactional
//    public ShoppingCartDto addProductsToCart(String username, Map<String, Integer> products) { // Меняем UUID на String!
//        validateUsername(username);
//        log.info("Adding products to cart for user: {}, products: {}", username, products);
//
//        Cart cart = cartRepository.findActiveCartWithItems(username)
//                .orElseGet(() -> createNewCart(username));
//
//        // Конвертируем String ключи в UUID
//        for (Map.Entry<String, Integer> entry : products.entrySet()) {
//            UUID productId;
//            try {
//                productId = UUID.fromString(entry.getKey());
//            } catch (IllegalArgumentException e) {
//                log.error("Invalid UUID format: {}", entry.getKey());
//                continue; // или выбросить исключение
//            }
//
//            Integer targetQuantity = entry.getValue();
//
//            cartItemRepository.findByCartShoppingCartIdAndProductId(cart.getShoppingCartId(), productId)
//                    .ifPresentOrElse(
//                            cartItem -> {
//                                cartItem.setQuantity(targetQuantity);
//                                cartItemRepository.save(cartItem);
//                            },
//                            () -> {
//                                CartItem newItem = new CartItem();
//                                newItem.setCart(cart);
//                                newItem.setProductId(productId);
//                                newItem.setQuantity(targetQuantity);
//                                cartItemRepository.save(newItem);
//                            }
//                    );
//        }
//
//        Cart updatedCart = cartRepository.findActiveCartWithItems(username)
//                .orElseThrow(() -> new CartNotFoundException(username, cart.getShoppingCartId()));
//
//        return shoppingCartMapper.toDto(updatedCart);
//    }


//    @Transactional
//    public ShoppingCartDto addProductsToCart(String username, Map<String, Integer> products) {
//        validateUsername(username);
//
//        log.info("=== CART DEBUG START ===");
//        log.info("Username: {}", username);
//        log.info("Received products map: {}", products);
//        log.info("Map size: {}", products.size());
//        log.info("Map type: {}", products.getClass().getName());
//
//        if (products != null && !products.isEmpty()) {
//            for (Map.Entry<String, Integer> entry : products.entrySet()) {
//                log.info("Key: '{}' (type: {}), Value: {} (type: {})",
//                        entry.getKey(), entry.getKey().getClass().getSimpleName(),
//                        entry.getValue(), entry.getValue().getClass().getSimpleName());
//
//                try {
//                    UUID productId = UUID.fromString(entry.getKey());
//                    log.info("Successfully parsed UUID: {}", productId);
//                } catch (Exception e) {
//                    log.error("Failed to parse UUID from: '{}'", entry.getKey(), e);
//                }
//            }
//        } else {
//            log.warn("Products map is null or empty!");
//        }
//        log.info("=== CART DEBUG END ===");
//
//        Cart cart = cartRepository.findActiveCartWithItems(username)
//                .orElseGet(() -> createNewCart(username));
//
//        int processedItems = 0;
//        for (Map.Entry<String, Integer> entry : products.entrySet()) {
//            UUID productId;
//            try {
//                productId = UUID.fromString(entry.getKey());
//            } catch (IllegalArgumentException e) {
//                log.error("Invalid UUID format: {}", entry.getKey());
//                continue;
//            }
//
//            Integer targetQuantity = entry.getValue();
//            log.info("Processing product: {} -> {}", productId, targetQuantity);
//
//            cartItemRepository.findByCartShoppingCartIdAndProductId(cart.getShoppingCartId(), productId)
//                    .ifPresentOrElse(
//                            cartItem -> {
//                                log.info("Updating existing item: {} from {} to {}",
//                                        productId, cartItem.getQuantity(), targetQuantity);
//                                cartItem.setQuantity(targetQuantity);
//                                cartItemRepository.save(cartItem);
//                            },
//                            () -> {
//                                log.info("Creating new item: {} with quantity {}",
//                                        productId, targetQuantity);
//                                CartItem newItem = new CartItem();
//                                newItem.setCart(cart);
//                                newItem.setProductId(productId);
//                                newItem.setQuantity(targetQuantity);
//                                cartItemRepository.save(newItem);
//                            }
//                    );
//            processedItems++;
//        }
//
//        log.info("Processed {} items", processedItems);
//
//        Cart updatedCart = cartRepository.findActiveCartWithItems(username)
//                .orElseThrow(() -> new CartNotFoundException(username, cart.getShoppingCartId()));
//
//        log.info("Final cart has {} items", updatedCart.getItems().size());
//        return shoppingCartMapper.toDto(updatedCart);
//    }

//    @Transactional
//    public ShoppingCartDto addProductsToCart(String username, Map<String, Integer> products) {
//        validateUsername(username);
//        log.info("Adding products to cart for user: {}, products: {}", username, products);
//
//        Cart cart = cartRepository.findActiveCartWithItems(username)
//                .orElseGet(() -> createNewCart(username));
//
//        for (Map.Entry<String, Integer> entry : products.entrySet()) {
//            UUID productId;
//            try {
//                productId = UUID.fromString(entry.getKey());
//            } catch (IllegalArgumentException e) {
//                log.error("Invalid UUID format: {}", entry.getKey());
//                continue;
//            }
//
//            Integer targetQuantity = entry.getValue();
//
//            cartItemRepository.findByCartShoppingCartIdAndProductId(cart.getShoppingCartId(), productId)
//                    .ifPresentOrElse(
//                            cartItem -> {
//                                cartItem.setQuantity(targetQuantity);
//                                cartItemRepository.save(cartItem);
//                            },
//                            () -> {
//                                CartItem newItem = new CartItem();
//                                newItem.setCart(cart);
//                                newItem.setProductId(productId);
//                                newItem.setQuantity(targetQuantity);
//                                cartItemRepository.save(newItem);
//                            }
//                    );
//        }
//
//        Cart updatedCart = cartRepository.findActiveCartWithItems(username)
//                .orElseThrow(() -> new CartNotFoundException(username, cart.getShoppingCartId()));
//
//        return shoppingCartMapper.toDto(updatedCart);
//    }

    @Transactional
    public ShoppingCartDto addProductsToCart(String username, Map<String, Integer> products) {
        validateUsername(username);
        log.info("Adding products to cart for user: {}, products: {}", username, products);

        Cart cart = cartRepository.findActiveCartWithItems(username)
                .orElseGet(() -> createNewCart(username));

        log.info("Found/created cart: {}, items count: {}", cart.getShoppingCartId(),
                cart.getItems() != null ? cart.getItems().size() : "null");

        for (Map.Entry<String, Integer> entry : products.entrySet()) {
            UUID productId;
            try {
                productId = UUID.fromString(entry.getKey());
            } catch (IllegalArgumentException e) {
                log.error("Invalid UUID format: {}", entry.getKey());
                continue;
            }

            Integer targetQuantity = entry.getValue();

            Optional<CartItem> existingItem = cartItemRepository.findByCartShoppingCartIdAndProductId(cart.getShoppingCartId(), productId);

            if (existingItem.isPresent()) {
                CartItem cartItem = existingItem.get();
                log.info("Updating existing item: {} from {} to {}",
                        productId, cartItem.getQuantity(), targetQuantity);
                cartItem.setQuantity(targetQuantity);
                cartItemRepository.save(cartItem);
            } else {
                log.info("Creating new item: {} with quantity {}", productId, targetQuantity);
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProductId(productId);
                newItem.setQuantity(targetQuantity);
                cartItemRepository.save(newItem);

                // ВАЖНО: Добавляем item в коллекцию cart.items
                if (cart.getItems() == null) {
                    cart.setItems(new ArrayList<>());
                }
                cart.getItems().add(newItem);
            }
        }

        // Сохраняем cart чтобы обновить связи
        cartRepository.save(cart);

        Cart updatedCart = cartRepository.findActiveCartWithItems(username)
                .orElseThrow(() -> new CartNotFoundException(username, cart.getShoppingCartId()));

        log.info("Final cart before mapping - ID: {}, items count: {}",
                updatedCart.getShoppingCartId(),
                updatedCart.getItems() != null ? updatedCart.getItems().size() : "null");

        return shoppingCartMapper.toDto(updatedCart);
    }

    @Transactional
    public void deactivateCart(String username) {
        validateUsername(username);
        log.info("Deactivating cart for user: {}", username);

        // ИЩЕМ активную корзину и деактивируем ее, если найдена
        cartRepository.findByUsernameAndStatus(username, Cart.CartStatus.ACTIVE)
                .ifPresent(cart -> {
                    cart.setStatus(Cart.CartStatus.DEACTIVATED);
                    cartRepository.save(cart);
                    log.info("Cart deactivated for user: {}", username);
                });

        // Если корзины не найдено - ничего не делаем (идемпотентность)
        log.info("No active cart found for user: {}, nothing to deactivate", username);
    }



    @Transactional
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> productIds) {
        validateUsername(username);
        log.info("Removing products from cart for user: {}, productIds: {}", username, productIds);

        Cart cart = cartRepository.findActiveCartWithItems(username)
                .orElseThrow(() -> new CartNotFoundException(username, null));

        // Проверяем, есть ли вообще товары в корзине
        if (cart.getItems().isEmpty()) {
            throw new NoProductsInShoppingCartException();
        }

        // Проверяем, что все удаляемые товары действительно есть в корзине
        List<UUID> cartProductIds = cart.getItems().stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toList());

        boolean allProductsExist = productIds.stream()
                .allMatch(cartProductIds::contains);

        if (!allProductsExist) {
            throw new NoProductsInShoppingCartException();
        }

        // Удаляем указанные товары
        cartItemRepository.deleteByCartIdAndProductIds(cart.getShoppingCartId(), productIds);

        // Получаем обновленную корзину
        Cart updatedCart = cartRepository.findActiveCartWithItems(username)
                .orElseThrow(() -> new CartNotFoundException(username, cart.getShoppingCartId()));

        return shoppingCartMapper.toDto(updatedCart);
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);
        log.info("Changing product quantity for user: {}, request: {}", username, request);

        Cart cart = cartRepository.findActiveCartWithItems(username)
                .orElseThrow(() -> new CartNotFoundException(username, null));

        CartItem cartItem = cartItemRepository.findByCartShoppingCartIdAndProductId(
                        cart.getShoppingCartId(), request.getProductId())
                .orElseThrow(() -> new NoProductsInShoppingCartException()); // ИСПРАВЛЕНО: без аргументов

        if (request.getNewQuantity() <= 0) {
            // Если количество 0 или меньше - удаляем товар
            cartItemRepository.delete(cartItem);
        } else {
            // Обновляем количество
            cartItem.setQuantity(request.getNewQuantity());
            cartItemRepository.save(cartItem);
        }

        // Получаем обновленную корзину
        Cart updatedCart = cartRepository.findActiveCartWithItems(username)
                .orElseThrow(() -> new CartNotFoundException(username, cart.getShoppingCartId()));

        return shoppingCartMapper.toDto(updatedCart);
    }

    private Cart createNewCart(String username) {
        Cart newCart = new Cart();
        newCart.setUsername(username);
        newCart.setStatus(Cart.CartStatus.ACTIVE);
        return cartRepository.save(newCart);
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new NotAuthorizedUserException(); // ИСПРАВЛЕНО: без аргументов
        }
    }
}