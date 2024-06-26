package ru.spbstu.ai.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.spbstu.ai.entity.Task;

import java.time.Duration;
import java.time.Instant;

public interface TaskService {
    Mono<Void> createTask(int telegramUserId, String summary, Instant deadline, Duration estimatedTime);
    Mono<Void> addSpentTime(int telegramUserId, int taskId, Duration spent);
    Mono<Void> markDone(int telegramUserId, int taskId);
    Mono<Task> getTaskById(int telegramUserId, int taskId);

    Mono<Void> markInProgress(int telegramUserId, int taskId);
    Mono<Void> setDeadline(int telegramUserId, int taskId, Instant deadline);

    Flux<Task> getByDeadline(int telegramUserId, Instant from, Instant to);
}
