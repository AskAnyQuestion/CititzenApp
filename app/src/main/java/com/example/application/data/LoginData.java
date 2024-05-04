package com.example.application.data;

public class LoginData {
    private Long phone;
    private String password;

    // Конструктор, геттеры и сеттеры
    public LoginData(Long phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }
}
