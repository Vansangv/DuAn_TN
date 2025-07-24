package com.example.demo.PhanQuyen;

public class LastLoggedInUser {
    private static String lastUsername;

    public static void setLastUsername(String username) {
        lastUsername = username;
    }

    public static String getLastUsername() {
        return lastUsername;
    }
}
