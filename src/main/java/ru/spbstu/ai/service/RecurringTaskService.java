package ru.spbstu.ai.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.spbstu.ai.entity.RecurringTask;

import java.time.Duration;
import java.time.Instant;

public interface RecurringTaskService {
    Mono<Void> createRecurring(int userId, String summary, Instant start, Duration period, Instant finish);
    Flux<RecurringTask> getInProgress(int userId);
    Flux<RecurringTask> getRecurrings(int userId);
    Mono<RecurringTask> getById(int userId, int taskId);

    Mono<Void> markDone(int userId, int taskId);
    Mono<Void> markInProgress(int userId, int taskId);
    Mono<Void> deleteRecurring(int userId, int taskId);

    Mono<Void> setSummary(int userId, int taskId, String summary);
    Mono<Void> reschedule(int userId, int taskId, Instant start, Duration period, Instant finish);
}
