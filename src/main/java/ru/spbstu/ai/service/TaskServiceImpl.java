package ru.spbstu.ai.service;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.types.YearToSecond;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.spbstu.ai.entity.Task;
import ru.spbstu.ai.entity.TaskStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.val;
import static ru.spbstu.ai.r2dbc.db.Tables.OWNER;
import static ru.spbstu.ai.r2dbc.db.Tables.TASK;

@Service
public class TaskServiceImpl implements TaskService {

    private final DSLContext ctx;

    public TaskServiceImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Mono<Void> createTask(int telegramUserId, String summary, Instant deadline, Duration estimatedTime) {
        return Mono.from(
                        ctx.insertInto(TASK).columns(TASK.OWNER_ID, TASK.SUMMARY, TASK.DEADLINE, TASK.ESTIMATED_TIME, TASK.STATUS, TASK.SPENT_TIME)
                                .select(select(OWNER.OWNER_ID, val(summary), val(deadline.atOffset(ZoneOffset.UTC)), val(YearToSecond.valueOf(estimatedTime)), val(0), val(YearToSecond.valueOf(0)))
                                        .from(OWNER)
                                        .where(OWNER.TELEGRAM_ID.eq(val(telegramUserId))))

                )
                .then();
    }


    @Override
    public Mono<Void> addSpentTime(int telegramUserId, int taskId, Duration spent) {
        return Mono.from(
                ctx.update(TASK).set(TASK.SPENT_TIME,
                                ctx.select(TASK.SPENT_TIME.add(YearToSecond.valueOf(spent)))
                                        .from(TASK.join(OWNER).on(TASK.OWNER_ID.eq(OWNER.OWNER_ID)))
                                        .where(TASK.TASK_ID.eq(taskId)))
                        .where(TASK.TASK_ID.eq(taskId).and(OWNER.TELEGRAM_ID.eq(telegramUserId)))
        ).then();
    }

    @Override
    public Mono<Void> markDone(int telegramUserId, int taskId) {
        return Mono.from(
                ctx.update(TASK).set(TASK.STATUS, 1)
                        .where(TASK.TASK_ID.eq(taskId).and(taskOwnerHas(telegramUserId)))
        ).then();
    }

    @Override
    public Mono<Task> getTaskById(int telegramUserId, int taskId) {
        return Flux.from(
                ctx.select(TASK.asterisk()).from(TASK.join(OWNER).on(TASK.OWNER_ID.eq(OWNER.OWNER_ID)))
                        .where(OWNER.TELEGRAM_ID.eq(telegramUserId)
                                .and(TASK.TASK_ID.eq(taskId)))
        ).map(x -> new Task(x.get(TASK.TASK_ID),
                x.get(TASK.SUMMARY),
                x.get(TASK.DEADLINE).toInstant(),
                TaskStatus.of(x.get(TASK.STATUS)),
                x.get(TASK.ESTIMATED_TIME).toDuration(),
                x.get(TASK.SPENT_TIME).toDuration())).singleOrEmpty();
    }

    @Override
    public Mono<Void> markInProgress(int telegramUserId, int taskId) {
        return Mono.from(
                ctx.update(TASK).set(TASK.STATUS, 0)
                        .where(TASK.TASK_ID.eq(taskId).and(taskOwnerHas(telegramUserId)))
        ).then();
    }

    @Override
    public Mono<Void> setDeadline(int telegramUserId, int taskId, Instant deadline) {
        return Mono.from(
                ctx.update(TASK).set(TASK.DEADLINE, deadline.atOffset(ZoneOffset.UTC))
                        .where(TASK.TASK_ID.eq(taskId).and(taskOwnerHas(telegramUserId)))
        ).then();
    }

    private static @NotNull Condition taskOwnerHas(int telegramUserId) {
        return TASK.OWNER_ID.eq(select(OWNER.OWNER_ID).from(OWNER).where(OWNER.TELEGRAM_ID.eq(telegramUserId)));
    }

    @Override
    public Flux<Task> getByDeadline(int telegramUserId, Instant from, Instant to) {
        return Flux.from(
                ctx.select(TASK.asterisk()).from(TASK.join(OWNER).on(TASK.OWNER_ID.eq(OWNER.OWNER_ID)))
                        .where(OWNER.TELEGRAM_ID.eq(telegramUserId)
                                .and(TASK.DEADLINE.between(from.atOffset(ZoneOffset.UTC), to.atOffset(ZoneOffset.UTC))))
        ).map(x -> new Task(x.get(TASK.TASK_ID),
                x.get(TASK.SUMMARY),
                x.get(TASK.DEADLINE).toInstant(),
                TaskStatus.of(x.get(TASK.STATUS)),
                x.get(TASK.ESTIMATED_TIME).toDuration(),
                x.get(TASK.SPENT_TIME).toDuration()));
    }
}
