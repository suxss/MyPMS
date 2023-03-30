package com.example.mypms.plugins;

public class Random {
    public static String randomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt((int) (Math.random() * str.length()));
            sb.append(ch);
        }
        return sb.toString();
    }
}
