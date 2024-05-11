package com.example.application.data;

public class UpdateData {
    private String oldLogin;
    private Long oldPhone;
    private String newLogin;
    private Long newPhone;

    public UpdateData(String oldLogin, Long oldPhone, String newLogin, Long newPhone) {
        this.oldLogin = oldLogin;
        this.oldPhone = oldPhone;
        this.newLogin = newLogin;
        this.newPhone = newPhone;
    }

    public String getOldLogin() {
        return oldLogin;
    }

    public void setOldLogin(String oldLogin) {
        this.oldLogin = oldLogin;
    }

    public Long getOldPhone() {
        return oldPhone;
    }

    public void setOldPhone(Long oldPhone) {
        this.oldPhone = oldPhone;
    }

    public String getNewLogin() {
        return newLogin;
    }

    public void setNewLogin(String newLogin) {
        this.newLogin = newLogin;
    }

    public Long getNewPhone() {
        return newPhone;
    }

    public void setNewPhone(Long newPhone) {
        this.newPhone = newPhone;
    }
}
