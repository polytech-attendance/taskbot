package ru.spbstu.ai.entity;

import ru.spbstu.ai.utils.DurationParser;
import ru.spbstu.ai.utils.InstantParser;

import java.time.Duration;
import java.time.Instant;

public record RecurringTask(
        Long id,
        String summary,
        Instant start,
        Duration period,
        Instant finish,
        TaskStatus status
) {

    public String toHumanReadableString() {
        return "Recurring task " + id + ":\n" +
                "\uD83D\uDCAC Summary: \"" + summary + "\"\n" +
                "\uD83D\uDCC5 Deadline: " + InstantParser.convertInstantToHumanReadableString(finish) + "\n" +
                "\uD83D\uDD0E Status: " + status.toHumanReadableString() + "\n" +
                "⏱ Last update: " + InstantParser.convertInstantToHumanReadableString(start) + "\n" +
                "⏱ Period: " + DurationParser.toHumanReadableString(period);

    }
}
