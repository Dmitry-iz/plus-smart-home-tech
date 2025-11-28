package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "warehouse_address", schema = "warehouse")
@Getter
@Setter
public class WarehouseAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String house;

    @Column(nullable = false) // Делаем обязательным
    private String flat; // Меняем apartment на flat

    // Статический метод для получения случайного адреса как в ТЗ
    public static WarehouseAddress getRandomAddress() {
        WarehouseAddress address = new WarehouseAddress();
        String addressValue = Math.random() > 0.5 ? "ADDRESS_1" : "ADDRESS_2";
        address.setCountry(addressValue);
        address.setCity(addressValue);
        address.setStreet(addressValue);
        address.setHouse(addressValue);
        address.setFlat(addressValue); // Теперь используем flat
        return address;
    }
}