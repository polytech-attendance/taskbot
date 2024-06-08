package ru.spbstu.ai.service;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.types.YearToSecond;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.spbstu.ai.entity.RecurringTask;
import ru.spbstu.ai.entity.TaskStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.jooq.impl.DSL.*;
import static ru.spbstu.ai.r2dbc.db.tables.RecurringTask.RECURRING_TASK;
import static ru.spbstu.ai.r2dbc.db.tables.Owner.OWNER;

@Service
public class RecurringTaskServiceImpl implements RecurringTaskService {

    private final DSLContext ctx;

    public RecurringTaskServiceImpl(DSLContext ctx) {
        this.ctx = ctx;
    }


    @Override
    public Mono<Void> createRecurring(int telegramUserId, String summary, Instant start, Duration period, Instant finish) {
        return Mono.from(ctx.insertInto(RECURRING_TASK).columns(RECURRING_TASK.OWNER_ID, RECURRING_TASK.SUMMARY, RECURRING_TASK.START, RECURRING_TASK.PERIOD, RECURRING_TASK.FINISH, RECURRING_TASK.STATUS)
                        .select(
                                select(OWNER.OWNER_ID, val(summary), val(start.atOffset(ZoneOffset.UTC)), val(YearToSecond.valueOf(period)), val(finish.atOffset(ZoneOffset.UTC)), val(0))
                                        .from(OWNER).where(OWNER.TELEGRAM_ID.eq(telegramUserId))))
                        .then();
    }

    @Override
    public Flux<RecurringTask> getInProgress(int telegramUserId) {
        return Flux.from(
                ctx.select(RECURRING_TASK.asterisk()).from(RECURRING_TASK)
                        .where(RECURRING_TASK.OWNER_ID.eq(
                                ownerId(telegramUserId)
                        ))
                                .and(RECURRING_TASK.STATUS.eq(0))
        ).map(x -> new RecurringTask(x.get(RECURRING_TASK.RECURRING_TASK_ID),
                        x.get(RECURRING_TASK.SUMMARY),
                        x.get(RECURRING_TASK.START).toInstant(),
                        x.get(RECURRING_TASK.PERIOD).toDuration(),
                        x.get(RECURRING_TASK.FINISH).toInstant(),
                        TaskStatus.of(x.get(RECURRING_TASK.STATUS))));
    }

    private static @NotNull SelectConditionStep<Record1<Integer>> ownerId(int telegramUserId) {
        return select(OWNER.OWNER_ID).from(OWNER).where(OWNER.TELEGRAM_ID.eq(telegramUserId));
    }

    @Override
    public Flux<RecurringTask> getRecurrings(int telegramUserId) {
        return Flux.from(
                ctx.select(asterisk()).from(RECURRING_TASK)
                        .where(RECURRING_TASK.OWNER_ID.eq(ownerId(telegramUserId)))
        ).map(x -> new RecurringTask(x.get(RECURRING_TASK.RECURRING_TASK_ID),
                x.get(RECURRING_TASK.SUMMARY),
                x.get(RECURRING_TASK.START).toInstant(),
                x.get(RECURRING_TASK.PERIOD).toDuration(),
                x.get(RECURRING_TASK.FINISH).toInstant(),
                TaskStatus.of(x.get(RECURRING_TASK.STATUS))));
    }

    @Override
    public Mono<RecurringTask> getById(int telegramUserId, int taskId) {
        return Flux.from(
                ctx.select(asterisk()).from(RECURRING_TASK)
                        .where(RECURRING_TASK.OWNER_ID.eq(ownerId(telegramUserId)))
                        .and(RECURRING_TASK.RECURRING_TASK_ID.eq(taskId))
        ).map(x -> new RecurringTask(x.get(RECURRING_TASK.RECURRING_TASK_ID),
                x.get(RECURRING_TASK.SUMMARY),
                x.get(RECURRING_TASK.START).toInstant(),
                x.get(RECURRING_TASK.PERIOD).toDuration(),
                x.get(RECURRING_TASK.FINISH).toInstant(),
                TaskStatus.of(x.get(RECURRING_TASK.STATUS)))).singleOrEmpty();
    }

    @Override
    public Mono<Void> markDone(int telegramUserId, int taskId) {
        return Mono.from(
                ctx.update(RECURRING_TASK).set(RECURRING_TASK.STATUS, 1).set(RECURRING_TASK.START, Instant.now().atOffset(ZoneOffset.UTC))
                        .where(RECURRING_TASK.RECURRING_TASK_ID.eq(taskId).and(RECURRING_TASK.OWNER_ID.eq(ownerId(telegramUserId))))
        ).then();
    }



    @Override
    public Mono<Void> markInProgress(int telegramUserId, int taskId) {
        return Mono.from(
                ctx.update(RECURRING_TASK).set(RECURRING_TASK.STATUS, 0).set(RECURRING_TASK.START, Instant.now().atOffset(ZoneOffset.UTC))
                        .where(RECURRING_TASK.RECURRING_TASK_ID.eq(taskId).and(RECURRING_TASK.OWNER_ID.eq(ownerId(telegramUserId))))
        ).then();
    }

    @Override
    public Mono<Void> deleteRecurring(int telegramUserId, int taskId) {
        return Mono.from(
                ctx.delete(RECURRING_TASK).where(RECURRING_TASK.RECURRING_TASK_ID.eq(taskId))
                        .and(RECURRING_TASK.OWNER_ID.eq(ownerId(telegramUserId)))
        ).then();
    }

    @Override
    public Mono<Void> setSummary(int telegramUserId, int taskId, String summary) {
        return Mono.from(
                ctx.update(RECURRING_TASK).set(RECURRING_TASK.SUMMARY, summary)
                        .where(RECURRING_TASK.RECURRING_TASK_ID.eq(taskId).and(RECURRING_TASK.OWNER_ID.eq(ownerId(telegramUserId))))
        ).then();
    }

    @Override
    public Mono<Void> reschedule(int telegramUserId, int taskId, Instant start, Duration period, Instant finish) {
        return Mono.from(
                ctx.update(RECURRING_TASK)
                        .set(RECURRING_TASK.START, start.atOffset(ZoneOffset.UTC))
                        .set(RECURRING_TASK.PERIOD, YearToSecond.valueOf(period))
                        .set(RECURRING_TASK.FINISH, finish.atOffset(ZoneOffset.UTC))
                        .where(RECURRING_TASK.RECURRING_TASK_ID.eq(taskId).and(RECURRING_TASK.OWNER_ID.eq(ownerId(telegramUserId))))
        ).then();
    }
}
