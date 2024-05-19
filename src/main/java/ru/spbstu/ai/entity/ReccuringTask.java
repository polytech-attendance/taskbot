package ru.spbstu.ai.entity;

import java.time.Duration;
import java.time.Instant;

public record ReccuringTask(
        Long ownerId,
        String summary,
        Instant start,
        Duration period,
        Instant finish,
        TaskStatus status
) {}
