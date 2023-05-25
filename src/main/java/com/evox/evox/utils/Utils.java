package com.evox.evox.utils;

public class Utils {
    public static String extractUsername(String refLink) {
        int position = refLink.lastIndexOf('/');
        return refLink.substring(position + 1);
    }
}
