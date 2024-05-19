package ru.spbstu.ai.entity;

import java.time.Duration;
import java.time.Instant;

public record Task(
        Long ownerId,
        String summary,
        Instant deadline,
        TaskStatus status,
        Duration estimatedTime,
        Duration spentTime
) {
}
