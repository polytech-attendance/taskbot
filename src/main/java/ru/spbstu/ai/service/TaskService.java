package ru.spbstu.ai.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.spbstu.ai.entity.Task;

import java.time.Duration;
import java.time.Instant;

public interface TaskService {
    Mono<Void> createTask(int userId, String summary, Instant deadline, Duration estimatedTime);
    Mono<Void> addSpentTime(int userId, int taskId, Duration spent);
    Mono<Void> markDone(int userId, int taskId);

    Mono<Void> markInProgress(int userId, int taskId);
    Mono<Void> setDeadline(int userId, int taskId, Instant deadline);

    Flux<Task> getByDeadline(int userId, Instant from, Instant to);
}
