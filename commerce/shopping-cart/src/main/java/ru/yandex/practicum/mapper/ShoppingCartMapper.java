package ru.yandex.practicum.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.entity.Cart;
import ru.yandex.practicum.entity.CartItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j

@Component
public class ShoppingCartMapper {

    public ShoppingCartDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setShoppingCartId(cart.getShoppingCartId());

        Map<String, Integer> products = new HashMap<>();
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                products.put(item.getProductId().toString(), item.getQuantity());
            }
        }
        dto.setProducts(products);

        log.info("Mapped cart {} with {} items: {}",
                cart.getShoppingCartId(),
                products.size(),
                products);
        return dto;
    }

    public Cart toEntity(ShoppingCartDto dto, String username) {
        if (dto == null) {
            return null;
        }

        Cart cart = new Cart();
        cart.setShoppingCartId(dto.getShoppingCartId());
        cart.setUsername(username);

        if (dto.getProducts() != null) {
            var items = dto.getProducts().entrySet().stream()
                    .map(entry -> {
                        CartItem item = new CartItem();
                        item.setCart(cart);
                        // КОНВЕРТИРУЕМ String в UUID
                        item.setProductId(UUID.fromString(entry.getKey()));
                        item.setQuantity(entry.getValue());
                        return item;
                    })
                    .collect(Collectors.toList());
            cart.setItems(items);
        }
        return cart;
    }
}