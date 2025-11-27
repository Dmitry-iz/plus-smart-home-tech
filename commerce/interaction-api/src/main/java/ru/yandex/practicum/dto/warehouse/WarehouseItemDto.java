////package ru.yandex.practicum.dto.warehouse;
////
////import lombok.AllArgsConstructor;
////import lombok.Data;
////import lombok.NoArgsConstructor;
////
////import java.math.BigDecimal;
////import java.util.UUID;
////
////@Data
////@NoArgsConstructor
////@AllArgsConstructor
////public class WarehouseItemDto {
////    private UUID id;
////    private UUID productId;
////    private Integer quantity;
////    private BigDecimal width;
////    private BigDecimal height;
////    private BigDecimal depth;
////    private BigDecimal weight;
////    private Boolean fragile;
////}
//
//
//package ru.yandex.practicum.dto.warehouse;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class WarehouseItemDto {
//    private UUID id;
//    private UUID productId;
//    private Integer quantity;
//
//    // Поля для прямого доступа
//    private BigDecimal width;
//    private BigDecimal height;
//    private BigDecimal depth;
//    private BigDecimal weight;
//    private Boolean fragile;
//
//    // Объект dimension для тестов
//    private Dimension dimension;
//
//    // Геттеры которые работают с обоими форматами
//    public BigDecimal getWidth() {
//        if (width != null) {
//            return width;
//        } else if (dimension != null && dimension.width != null) {
//            return dimension.width;
//        }
//        return null;
//    }
//
//    public BigDecimal getHeight() {
//        if (height != null) {
//            return height;
//        } else if (dimension != null && dimension.height != null) {
//            return dimension.height;
//        }
//        return null;
//    }
//
//    public BigDecimal getDepth() {
//        if (depth != null) {
//            return depth;
//        } else if (dimension != null && dimension.depth != null) {
//            return dimension.depth;
//        }
//        return null;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class Dimension {
//        private BigDecimal width;
//        private BigDecimal height;
//        private BigDecimal depth;
//    }
//}