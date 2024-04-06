package com.example.application;
public class Utils {
    public static boolean isNumber(String str) {
        try {
            Long phone = Long.parseLong("8".concat(str));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
