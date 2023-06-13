package com.evox.evox.utils;

import java.util.UUID;

public class Utils {
    public static String extractUsername(String refLink) {
        int position = refLink.lastIndexOf('/');
        return refLink.substring(position + 1);
    }

    public static String uid() {
        return UUID.randomUUID().toString().substring(0,8);
    }
}
