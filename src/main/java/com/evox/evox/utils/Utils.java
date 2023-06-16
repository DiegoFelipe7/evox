package com.evox.evox.utils;

import java.util.Map;
import java.util.UUID;

public class Utils {
    public static String extractUsername(String refLink) {
        int position = refLink.lastIndexOf('/');
        return refLink.substring(position + 1);
    }

    public static String uid() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static Double bonus(Integer level) {
        Map<Integer, Double> bonusMap = Map.of(
                1, 0.4,
                2, 0.3,
                3, 0.2,
                4, 0.1
        );
        return bonusMap.getOrDefault(level, 0.0);
    }

}
