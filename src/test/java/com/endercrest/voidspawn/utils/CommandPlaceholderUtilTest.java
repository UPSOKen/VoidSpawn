package com.endercrest.voidspawn.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CommandPlaceholderUtilTest {
    @Test
    public void applySupportsDollarBracedPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player.name", "Notch");
        placeholders.put("player.uuid", "069a79f4-44e9-4726-a5be-fca90e38aaf5");
        placeholders.put("player.touch.world", "world");

        String command = CommandPlaceholderUtil.apply(
                "rtp ${player.name};say ${player.uuid};spawn ${player.touch.world}",
                placeholders
        );

        assertThat(command, is("rtp Notch;say 069a79f4-44e9-4726-a5be-fca90e38aaf5;spawn world"));
    }
}
