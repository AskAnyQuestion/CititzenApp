package com.example.application.model;

import com.example.application.Utils;

public class User {
    String login;
    String password;
    Long phone;
    String ipv4;

    public User() {
    }

    public User(Long phone, String login, String password) {
        this.login = login;
        this.password = password;
        this.phone = phone;
        this.ipv4 = Utils.getLocalIPAddress(true);
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
