package ru.spbstu.ai.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationParser {
    public static Duration parsePeriod(String input) {
        return switch (input.toLowerCase()) {
            case "hourly" -> Duration.ofHours(1);
            case "daily" -> Duration.ofDays(1);
            case "weekly" -> Duration.ofDays(7);
            case "monthly" -> Duration.ofDays(30);
            default -> throw new IllegalArgumentException("Invalid duration format: " + input);
        };
    }


    public static String toHumanReadableString(Duration duration) {
        if(duration.equals(Duration.ZERO)) {
            return "0";
        }

        long days = duration.toDays();
        duration = duration.minusDays(days);

        long hours = duration.toHours();
        duration = duration.minusHours(hours);

        long minutes = duration.toMinutes();

        List<String> parts = new ArrayList<>();
        if (days > 0) {
            parts.add(days + " day" + (days > 1 ? "s" : ""));
        }
        if (hours > 0) {
            parts.add(hours + " hour" + (hours > 1 ? "s" : ""));
        }
        if (minutes > 0) {
            parts.add(minutes + " minute" + (minutes > 1 ? "s" : ""));
        }

        return String.join(" ", parts);
    }

    public static Duration parse(String input) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*(day|hour|minute)(s?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        Duration duration = Duration.ZERO;

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            duration = switch (unit) {
                case "day" -> duration.plusDays(value);
                case "hour" -> duration.plusHours(value);
                case "minute" -> duration.plusMinutes(value);
                default -> duration;
            };
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
