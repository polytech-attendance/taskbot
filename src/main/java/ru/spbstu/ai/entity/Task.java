package ru.spbstu.ai.entity;

import ru.spbstu.ai.utils.DurationParser;
import ru.spbstu.ai.utils.InstantParser;

import java.time.Duration;
import java.time.Instant;

public record Task(
        int id,
        String summary,
        Instant deadline,
        TaskStatus status,
        Duration estimatedTime,
        Duration spentTime
) {
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", summary='" + summary + '\'' +
                ", deadline=" + deadline +
                ", status=" + status +
                ", estimatedTime=" + DurationParser.toHumanReadableString(estimatedTime) +
                ", spentTime=" + DurationParser.toHumanReadableString(spentTime) +
                '}';
    }

    public String toHumanReadableString() {
        return "Task " + id + ":\n" +
                "\uD83D\uDCAC Summary: \"" + summary + "\"\n" +
                "\uD83D\uDCC5 Deadline: " + InstantParser.convertInstantToHumanReadableString(deadline) + "\n" +
                "\uD83D\uDD0E Status: " + status.toHumanReadableString() + "\n" +
                "⏱ Estimated time: " + DurationParser.toHumanReadableString(estimatedTime) + "\n" +
                "⏱ Spent time: " + DurationParser.toHumanReadableString(spentTime);

    }
}
