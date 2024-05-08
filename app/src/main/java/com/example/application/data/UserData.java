package com.example.application.data;

public class UserData {
    private Long phone;
    private String login;

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public UserData(Long phone, String login) {
        this.phone = phone;
        this.login = login;
    }
}
