package ru.spbstu.ai.entity;

import ru.spbstu.ai.utils.DurationParser;

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
}
