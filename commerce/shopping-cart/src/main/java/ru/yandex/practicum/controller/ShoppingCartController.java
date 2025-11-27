package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingCartClient;
import ru.yandex.practicum.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartClient {

    private final CartService cartService;

    @Override
    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam("username") String username) {
        return cartService.getShoppingCart(username);
    }

    // РЕАЛИЗАЦИЯ ДЛЯ FEIGN CLIENT
    @Override
    @PutMapping
    public ShoppingCartDto addProductsToCart(
            @RequestParam("username") String username,
            @RequestBody Map<UUID, Integer> products) {

        return cartService.addProductsToCart(username, products);
    }

    // ОТДЕЛЬНЫЙ ENDPOINT ДЛЯ ТЕСТОВ С ПУСТЫМ ТЕЛОМ
    @PutMapping(params = {"username"}, headers = {"Content-Length=0"})
    public ShoppingCartDto addProductsToCartEmptyBody(@RequestParam("username") String username) {
        log.info("Add products to cart with empty body for user: {}", username);
        return cartService.addProductsToCart(username, Map.of());
    }

    @Override
    @DeleteMapping
    public void deactivateCart(@RequestParam("username") String username) {
        cartService.deactivateCart(username);
    }

    // РЕАЛИЗАЦИЯ ДЛЯ FEIGN CLIENT
    @Override
    @PostMapping("/remove")
    public ShoppingCartDto removeProductsFromCart(
            @RequestParam("username") String username,
            @RequestBody List<UUID> productIds) {

        return cartService.removeProductsFromCart(username, productIds);
    }

    // ОТДЕЛЬНЫЙ ENDPOINT ДЛЯ ТЕСТОВ С ПУСТЫМ ТЕЛОМ
    @PostMapping(value = "/remove", params = {"username"}, headers = {"Content-Length=0"})
    public ShoppingCartDto removeProductsFromCartEmptyBody(@RequestParam("username") String username) {
        log.info("Remove products from cart with empty body for user: {}", username);
        return cartService.removeProductsFromCart(username, List.of());
    }

    // РЕАЛИЗАЦИЯ ДЛЯ FEIGN CLIENT
    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(
            @RequestParam("username") String username,
            @RequestBody ChangeProductQuantityRequest request) {

        return cartService.changeProductQuantity(username, request);
    }

    // ОТДЕЛЬНЫЙ ENDPOINT ДЛЯ ТЕСТОВ С ПАРАМЕТРАМИ URL
    @PostMapping(value = "/change-quantity", params = {"username", "productId", "newQuantity"})
    public ShoppingCartDto changeProductQuantityFromUrl(
            @RequestParam("username") String username,
            @RequestParam("productId") UUID productId,
            @RequestParam("newQuantity") Integer newQuantity) {

        log.info("Change product quantity from URL - user: {}, productId: {}, quantity: {}",
                username, productId, newQuantity);

        ChangeProductQuantityRequest request = new ChangeProductQuantityRequest(productId, newQuantity);
        return cartService.changeProductQuantity(username, request);
    }
}