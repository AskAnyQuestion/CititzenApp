package com.example.application.model;

public class User {
    String login;
    String password;
    Long phone;

    public User() {
    }

    public User(Long phone, String login, String password) {
        this.login = login;
        this.password = password;
        this.phone = phone;
    }
}
