package com.example.application.exception;

import org.jetbrains.annotations.NotNull;

public enum CLIENT {
    FILL_FIELD("Пожалуйста, заполните все поля"),
    LONG_VALUE("Слишком длинное значение поля"),
    INVALID_PHONE_NUMBER("Некорректный номер телефона"),
    PASSWORD_MISMATCH("Пароли не совпадают"),
    SEND_DATA("Подтвердите согласие на обработку данных"),
    MATERIAL_NOT_FOUND("Материал не загружен");

    final String s;
    CLIENT(String s) {
        this.s = s;
    }

    @NotNull
    @Override
    public String toString() {
        return s;
    }
}
