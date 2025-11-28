package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.entity.Cart;
import ru.yandex.practicum.entity.CartItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

//@Component
//public class ShoppingCartMapper {
//
//    public ShoppingCartDto toDto(Cart cart) {
//        if (cart == null) {
//            return null;
//        }
//
//        ShoppingCartDto dto = new ShoppingCartDto();
//        dto.setShoppingCartId(cart.getShoppingCartId());
//
//        // Преобразуем List<CartItem> в Map<UUID, Integer>
//        Map<UUID, Integer> products = cart.getItems().stream()
//                .collect(Collectors.toMap(
//                        CartItem::getProductId,
//                        CartItem::getQuantity
//                ));
//        dto.setProducts(products);
//
//        return dto;
//    }
//
//    public Cart toEntity(ShoppingCartDto dto, String username) {
//        if (dto == null) {
//            return null;
//        }
//
//        Cart cart = new Cart();
//        cart.setShoppingCartId(dto.getShoppingCartId());
//        cart.setUsername(username);
//
//        // Преобразуем Map<UUID, Integer> в List<CartItem>
//        if (dto.getProducts() != null) {
//            var items = dto.getProducts().entrySet().stream()
//                    .map(entry -> {
//                        CartItem item = new CartItem();
//                        item.setCart(cart);
//                        item.setProductId(entry.getKey());
//                        item.setQuantity(entry.getValue());
//                        return item;
//                    })
//                    .collect(Collectors.toList());
//            cart.setItems(items);
//        }
//
//        return cart;
//    }
//}

@Component
public class ShoppingCartMapper {

    public ShoppingCartDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setShoppingCartId(cart.getShoppingCartId());

        // КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ: Правильно заполняем Map
        Map<UUID, Integer> products = new HashMap<>();
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                products.put(item.getProductId(), item.getQuantity());
            }
        }
        dto.setProducts(products);

        // log.info("Mapped cart {} with {} items", cart.getShoppingCartId(), products.size());
        return dto;
    }

    public Cart toEntity(ShoppingCartDto dto, String username) {
        if (dto == null) {
            return null;
        }

        Cart cart = new Cart();
        cart.setShoppingCartId(dto.getShoppingCartId());
        cart.setUsername(username);

        // Преобразуем Map<UUID, Integer> в List<CartItem>
        if (dto.getProducts() != null) {
            var items = dto.getProducts().entrySet().stream()
                    .map(entry -> {
                        CartItem item = new CartItem();
                        item.setCart(cart);
                        item.setProductId(entry.getKey());
                        item.setQuantity(entry.getValue());
                        return item;
                    })
                    .collect(Collectors.toList());
            cart.setItems(items);
        }

        return cart;
    }
}