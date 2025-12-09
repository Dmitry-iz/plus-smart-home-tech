//package ru.yandex.practicum.client;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
//
//@FeignClient(name = "shopping-cart")
//public interface ShoppingCartClient {
//    @GetMapping("/api/v1/shopping-cart")
//    ShoppingCartDto getShoppingCart(@RequestParam("username") String username);
//}


//// order/src/main/java/ru/yandex/practicum/client/ShoppingCartClient.java
//package ru.yandex.practicum.client;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
//
//@FeignClient(
//        name = "shopping-cart",
//        contextId = "orderShoppingCartClient",
//        url = "${feign.client.shopping-cart.url}"
//)
//public interface ShoppingCartClient {
//
//    @GetMapping("/api/v1/shopping-cart")
//    ShoppingCartDto getShoppingCart(@RequestParam("username") String username);
//
//    @DeleteMapping("/api/v1/shopping-cart")
//    void deactivateCart(@RequestParam("username") String username);
//}