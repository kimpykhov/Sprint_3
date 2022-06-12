package ru.yandex.sprint3;

public class AuthCourier {
    public final String login;
    public final String password;
    public int id;

    public AuthCourier(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
