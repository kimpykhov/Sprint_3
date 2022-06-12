package ru.yandex.sprint3;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    public final String firstName;
    public final String lastName;
    public final String address;
    public final String metroStation;
    public final String phone;
    public final int rentTime;
    public final String deliveryDat;
    public final String comment;
    public final List<String> color;


    public Order(List<String>color) {
        this.firstName = "Naruto";
        this.lastName = "Uchiha";
        this.address = "Konoha, 142 apt.";
        this.metroStation = "Kurskaia";
        this.phone = "+7 800 355 35 35";
        this.rentTime = 5;
        this.deliveryDat = LocalDateTime.now().toString();
        this.comment = "Комментарий для курьера";
        this.color = color;
    }
}