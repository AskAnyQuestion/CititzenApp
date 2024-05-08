package com.example.application.model;

import com.example.application.Utils;

public class User {
    String login;
    String password;
    Long phone;
    String ipv4;
    String token;

    public User() {
    }

    public User(Long phone, String login, String password, String token) {
        this(phone, login);
        this.password = password;
        this.token = token;
    }
    public User(Long phone, String login) {
        this.login = login;
        this.phone = phone;
        this.ipv4 = Utils.getLocalIPAddress(true);
    }


    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
