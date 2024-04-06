package com.example.application.exception;

import org.jetbrains.annotations.NotNull;

public enum VALIDATE_FIELD {
    FILL_FIELD("Пожалуйста, заполните все поля"),
    LONG_VALUE("Слишком длинное значение поля"),
    INVALID_PHONE_NUMBER("Некорректный номер телефона"),
    PASSWORD_MISMATCH("Пароли не совпадают");

    final String s;
    VALIDATE_FIELD(String s) {
        this.s = s;
    }

    @NotNull
    @Override
    public String toString() {
        return s;
    }
}
