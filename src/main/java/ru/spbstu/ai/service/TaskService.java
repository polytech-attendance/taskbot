package ru.spbstu.ai.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.spbstu.ai.entity.Task;

import java.time.Duration;
import java.time.Instant;

public interface TaskService {
    Mono<Task> createTask(String summary, Instant deadline, Duration estimatedTime);
    Mono<Task> addSpentTime(Long taskId, Duration spent);
    Mono<Task> markDone(Long taskId);

    Mono<Task> markInProgress(Long taskId);
    Mono<Task> setDeadline(Long taskId, Instant deadline);

    Flux<Task> getByDeadline(Instant from, Instant to);
}
