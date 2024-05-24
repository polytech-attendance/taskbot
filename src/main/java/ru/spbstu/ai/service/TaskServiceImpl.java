package ru.spbstu.ai.service;

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

import static org.jooq.impl.DSL.asterisk;
import static ru.spbstu.ai.r2dbc.db.tables.Task.TASK;

@Service
public class TaskServiceImpl implements TaskService {

    private final DSLContext ctx;

    public TaskServiceImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Mono<Void> createTask(int userId, String summary, Instant deadline, Duration estimatedTime) {
        return Mono.from(ctx.insertInto(TASK).columns(TASK.OWNER_ID, TASK.SUMMARY, TASK.DEADLINE, TASK.ESTIMATED_TIME, TASK.STATUS, TASK.SPENT_TIME)
                .values(userId, summary, deadline.atOffset(ZoneOffset.UTC), YearToSecond.valueOf(estimatedTime), 0, YearToSecond.valueOf(0)))
                .then();
    }


    @Override
    public Mono<Void> addSpentTime(int userId, int taskId, Duration spent) {
        return Mono.from(
            ctx.update(TASK).set(TASK.SPENT_TIME,
                            ctx.select(TASK.SPENT_TIME.add(YearToSecond.valueOf(spent)))
                                    .from(TASK).where(TASK.TASK_ID.eq(taskId)))
                    .where(TASK.TASK_ID.eq(taskId).and(TASK.OWNER_ID.eq(userId)))
            ).then();
    }

    @Override
    public Mono<Void> markDone(int userId, int taskId) {
        return Mono.from(
                ctx.update(TASK).set(TASK.STATUS, 1)
                        .where(TASK.TASK_ID.eq(taskId).and(TASK.OWNER_ID.eq(userId)))
        ).then();
    }

    @Override
    public Mono<Task> getTaskById(int userId, int taskId) {
        return Flux.from(
                ctx.select(asterisk()).from(TASK)
                        .where(TASK.OWNER_ID.eq(userId)
                                .and(TASK.TASK_ID.eq(taskId)))
        ).map(x -> new Task(x.get(TASK.TASK_ID),
                x.get(TASK.SUMMARY),
                x.get(TASK.DEADLINE).toInstant(),
                TaskStatus.of(x.get(TASK.STATUS)),
                x.get(TASK.ESTIMATED_TIME).toDuration(),
                x.get(TASK.SPENT_TIME).toDuration())).singleOrEmpty();
    }

    @Override
    public Mono<Void> markInProgress(int userId, int taskId) {
        return Mono.from(
                ctx.update(TASK).set(TASK.STATUS, 0)
                        .where(TASK.TASK_ID.eq(taskId).and(TASK.OWNER_ID.eq(userId)))
        ).then();
    }

    @Override
    public Mono<Void> setDeadline(int userId, int taskId, Instant deadline) {
        return Mono.from(
                ctx.update(TASK).set(TASK.DEADLINE, deadline.atOffset(ZoneOffset.UTC))
                        .where(TASK.TASK_ID.eq(taskId).and(TASK.OWNER_ID.eq(userId)))
        ).then();
    }

    @Override
    public Flux<Task> getByDeadline(int userId, Instant from, Instant to) {
        return Flux.from(
                ctx.select(asterisk()).from(TASK)
                        .where(TASK.OWNER_ID.eq(userId)
                                .and(TASK.DEADLINE.between(from.atOffset(ZoneOffset.UTC), to.atOffset(ZoneOffset.UTC))))
        ).map(x -> new Task(x.get(TASK.TASK_ID),
                x.get(TASK.SUMMARY),
                x.get(TASK.DEADLINE).toInstant(),
                TaskStatus.of(x.get(TASK.STATUS)),
                x.get(TASK.ESTIMATED_TIME).toDuration(),
                x.get(TASK.SPENT_TIME).toDuration()));
    }
}
