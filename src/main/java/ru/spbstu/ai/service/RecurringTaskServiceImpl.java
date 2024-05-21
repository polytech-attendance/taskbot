package ru.spbstu.ai.service;

import org.jooq.DSLContext;
import org.jooq.types.YearToSecond;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.spbstu.ai.entity.RecurringTask;
import ru.spbstu.ai.entity.Task;
import ru.spbstu.ai.entity.TaskStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.user;
import static ru.spbstu.ai.r2dbc.db.tables.RecurringTask.RECURRING_TASK;
import static ru.spbstu.ai.r2dbc.db.tables.Task.TASK;

@Service
public class RecurringTaskServiceImpl implements RecurringTaskService {

    @Autowired
    private DSLContext ctx;


    @Override
    public Mono<Void> createRecurring(int userId, String summary, Instant start, Duration period, Instant finish) {
        return Mono.from(ctx.insertInto(RECURRING_TASK).columns(RECURRING_TASK.OWNER_ID, RECURRING_TASK.SUMMARY, RECURRING_TASK.START, RECURRING_TASK.PERIOD, RECURRING_TASK.FINISH, RECURRING_TASK.STATUS)
                        .values(userId, summary, start.atOffset(ZoneOffset.UTC), YearToSecond.valueOf(period), finish.atOffset(ZoneOffset.UTC), 0))
                        .then();
    }

    @Override
    public Flux<RecurringTask> getInProgress(int userId) {
        return Flux.from(
                ctx.select(asterisk()).from(RECURRING_TASK)
                        .where(RECURRING_TASK.OWNER_ID.eq(userId))
                                .and(RECURRING_TASK.STATUS.eq(0))
        ).map(x -> new RecurringTask(Long.valueOf(x.get(RECURRING_TASK.RECURRING_TASK_ID)),
                        x.get(RECURRING_TASK.SUMMARY),
                        x.get(RECURRING_TASK.FINISH).toInstant(),
                        x.get(RECURRING_TASK.PERIOD).toDuration(),
                        x.get(RECURRING_TASK.START).toInstant(),
                        TaskStatus.of(x.get(RECURRING_TASK.STATUS))));
    }

    @Override
    public Flux<RecurringTask> getRecurrings(int userId) {
        return Flux.from(
                ctx.select(asterisk()).from(RECURRING_TASK)
                        .where(RECURRING_TASK.OWNER_ID.eq(userId))
        ).map(x -> new RecurringTask(Long.valueOf(x.get(RECURRING_TASK.RECURRING_TASK_ID)),
                x.get(RECURRING_TASK.SUMMARY),
                x.get(RECURRING_TASK.FINISH).toInstant(),
                x.get(RECURRING_TASK.PERIOD).toDuration(),
                x.get(RECURRING_TASK.START).toInstant(),
                TaskStatus.of(x.get(RECURRING_TASK.STATUS))));
    }

    @Override
    public Mono<Void> markDone(int userId, int taskId) {
        return Mono.from(
                ctx.update(RECURRING_TASK).set(RECURRING_TASK.STATUS, 1)
                        .where(RECURRING_TASK.RECURRING_TASK_ID.eq(taskId).and(RECURRING_TASK.OWNER_ID.eq(userId)))
        ).then();
    }



    @Override
    public Mono<RecurringTask> markInProgress(int userId, int taskId) {
        return null;
    }

    @Override
    public Mono<Void> deleteRecurring(int userId, int taskId) {
        return Mono.from(
                ctx.delete(RECURRING_TASK).where(RECURRING_TASK.RECURRING_TASK_ID.eq(taskId))
                        .and(RECURRING_TASK.OWNER_ID.eq(userId))
        ).then();
    }

    @Override
    public Mono<Void> setSummary(int userId, int taskId, String summary) {
        return Mono.from(
                ctx.update(RECURRING_TASK).set(RECURRING_TASK.SUMMARY, summary)
                        .where(RECURRING_TASK.RECURRING_TASK_ID.eq(taskId).and(RECURRING_TASK.OWNER_ID.eq(userId)))
        ).then();
    }

    @Override
    public Mono<Void> reschedule(int userId, int taskId, Instant start, Duration period, Instant finish) {
        return Mono.from(
                ctx.update(RECURRING_TASK)
                        .set(RECURRING_TASK.START, start.atOffset(ZoneOffset.UTC))
                        .set(RECURRING_TASK.PERIOD, YearToSecond.valueOf(period))
                        .set(RECURRING_TASK.FINISH, finish.atOffset(ZoneOffset.UTC))
                        .where(RECURRING_TASK.RECURRING_TASK_ID.eq(taskId).and(RECURRING_TASK.OWNER_ID.eq(userId)))
        ).then();
    }
}
