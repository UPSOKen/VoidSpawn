package com.endercrest.voidspawn.utils;

import java.util.Map;

public final class CommandPlaceholderUtil {
    private CommandPlaceholderUtil() {
    }

    public static String apply(String command, Map<String, String> placeholders) {
        String result = command;
        for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            result = result.replace("${" + placeholder.getKey() + "}", placeholder.getValue());
        }
        return result;
    }
}
