package ru.yandex.practicum.mapper;

//import org.mapstruct.BeanMapping;
//import org.mapstruct.Mapper;
//import org.mapstruct.MappingTarget;
//import org.mapstruct.NullValuePropertyMappingStrategy;
//import ru.yandex.practicum.dto.shoppingstore.ProductDto;
//import ru.yandex.practicum.entity.Product;
//
//@Mapper(componentModel = "spring")
//public interface ProductMapper {
//
//    ProductDto toDto(Product product);
//
//    Product toEntity(ProductDto dto);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateEntityFromDto(ProductDto dto, @MappingTarget Product product);
//}


//package ru.yandex.practicum.mapper;
//
//import org.mapstruct.BeanMapping;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.MappingTarget;
//import org.mapstruct.NullValuePropertyMappingStrategy;
//import ru.yandex.practicum.dto.shoppingstore.ProductDto;
//import ru.yandex.practicum.entity.Product;
//
//@Mapper(componentModel = "spring")
//public interface ProductMapper {
//
//    ProductDto toDto(Product product);
//
//    @Mapping(target = "productId", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
//    Product toEntity(ProductDto dto);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateEntityFromDto(ProductDto dto, @MappingTarget Product product);
//}


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductDto dto);

    ProductDto toDto(Product product); // здесь не нужно ignore - в Dto нет этих полей

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ProductDto dto, @MappingTarget Product product);
}