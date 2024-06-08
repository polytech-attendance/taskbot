package ru.spbstu.ai.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.spbstu.ai.entity.RecurringTask;

import java.time.Duration;
import java.time.Instant;

public interface RecurringTaskService {
    Mono<Void> createRecurring(int telegramUserId, String summary, Instant start, Duration period, Instant finish);
    Flux<RecurringTask> getInProgress(int telegramUserId);
    Flux<RecurringTask> getRecurrings(int telegramUserId);
    Mono<RecurringTask> getById(int telegramUserId, int taskId);

    Mono<Void> markDone(int telegramUserId, int taskId);
    Mono<Void> markInProgress(int telegramUserId, int taskId);
    Mono<Void> deleteRecurring(int telegramUserId, int taskId);

    Mono<Void> setSummary(int telegramUserId, int taskId, String summary);
    Mono<Void> reschedule(int telegramUserId, int taskId, Instant start, Duration period, Instant finish);
}
