package com.example.device.api.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

    /**
     * Trim and convert empty strings to null.
     */
    public static String normalize(String value) {
        if (value == null) return null;

        value = value.trim();
        return value.isEmpty() ? null : value;
    }
}
