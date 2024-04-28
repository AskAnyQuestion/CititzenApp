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

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Long getPhone() {
        return phone;
    }
}
