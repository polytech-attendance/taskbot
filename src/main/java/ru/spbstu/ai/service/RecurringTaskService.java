package ru.spbstu.ai.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.spbstu.ai.entity.RecurringTask;

import java.time.Duration;
import java.time.Instant;

public interface RecurringTaskService {
    Mono<RecurringTask> createRecurring(String summary, Instant start, Duration period, Instant finish);
    Flux<RecurringTask> getInProgress();

    Mono<RecurringTask> markDone(Long id);
    Mono<RecurringTask> markInProgress(Long id);

    Mono<RecurringTask> setSummary(Long id, String summary);
    Mono<RecurringTask> reschedule(Long id, Instant start, Duration period, Instant finish);
}
