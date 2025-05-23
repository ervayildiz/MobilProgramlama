package com.example.tarif.activities.utils;

public class ValidationUtil {
    public static boolean isValidEmail(String email) {
        return email != null && email.matches(".+@.+\\..+");
    }

    public static boolean isValidPassword(String password) {
        return password != null &&
                password.length() >= 8 &&
                password.matches(".*[A-Za-z].*") &&
                password.matches(".*[0-9].*");
    }
}