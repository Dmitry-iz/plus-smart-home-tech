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

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Transactional
    public ShoppingCartDto addProductsToCart(String username, Map<UUID, Integer> products) {
        validateUsername(username);
        log.info("Adding products to cart for user: {}, products: {}", username, products);

        Cart cart = cartRepository.findActiveCartWithItems(username)
                .orElseGet(() -> createNewCart(username));

        // Добавляем или обновляем товары в корзине
        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            cartItemRepository.findByCartShoppingCartIdAndProductId(cart.getShoppingCartId(), productId)
                    .ifPresentOrElse(
                            cartItem -> {
                                // Обновляем количество если товар уже есть
                                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                                cartItemRepository.save(cartItem);
                            },
                            () -> {
                                // Добавляем новый товар
                                CartItem newItem = new CartItem();
                                newItem.setCart(cart);
                                newItem.setProductId(productId);
                                newItem.setQuantity(quantity);
                                cartItemRepository.save(newItem);
                            }
                    );
        }

        // Обновляем корзину
        Cart updatedCart = cartRepository.findActiveCartWithItems(username)
                .orElseThrow(() -> new CartNotFoundException(username, cart.getShoppingCartId()));

        return shoppingCartMapper.toDto(updatedCart);
    }

//    @Transactional
//    public void deactivateCart(String username) {
//        validateUsername(username);
//        log.info("Deactivating cart for user: {}", username);
//
//        Cart cart = cartRepository.findByUsernameAndStatus(username, Cart.CartStatus.ACTIVE)
//                .orElseThrow(() -> new CartNotFoundException(username, null));
//
//        cart.setStatus(Cart.CartStatus.DEACTIVATED);
//        cartRepository.save(cart);
//    }

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

        if (cart.getItems().isEmpty()) {
            throw new NoProductsInShoppingCartException(); // ИСПРАВЛЕНО: без аргументов
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