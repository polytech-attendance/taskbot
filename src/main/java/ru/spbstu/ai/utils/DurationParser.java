package ru.spbstu.ai.utils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationParser {

    public static Duration parse(String input) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*(day|hour|minute)(s?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        Duration duration = Duration.ZERO;

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            switch (unit) {
                case "day":
                    duration = duration.plusDays(value);
                    break;
                case "hour":
                    duration = duration.plusHours(value);
                    break;
                case "minute":
                    duration = duration.plusMinutes(value);
                    break;
            }
        }

        return duration;
    }

    public static void main(String[] args) {
        System.out.println(parse("2 days"));
        System.out.println(parse("5 hours"));
        System.out.println(parse("15 minutes"));
        System.out.println(parse("1 day 3 hours 20 minutes"));
    }
}
