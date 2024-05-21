package ru.spbstu.ai.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InstantParser {
    public static String convertInstantToHumanReadableString(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        // Форматируем Instant в строку
        return formatter.format(instant);
    }
}
