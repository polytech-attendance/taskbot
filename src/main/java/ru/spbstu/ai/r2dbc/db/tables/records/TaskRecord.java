/*
 * This file is generated by jOOQ.
 */
package ru.spbstu.ai.r2dbc.db.tables.records;


import java.time.OffsetDateTime;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.YearToSecond;

import ru.spbstu.ai.r2dbc.db.tables.Task;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class TaskRecord extends UpdatableRecordImpl<TaskRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.task.task_id</code>.
     */
    public void setTaskId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.task.task_id</code>.
     */
    public Integer getTaskId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.task.owner_id</code>.
     */
    public void setOwnerId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.task.owner_id</code>.
     */
    public Integer getOwnerId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.task.summary</code>.
     */
    public void setSummary(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.task.summary</code>.
     */
    public String getSummary() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.task.deadline</code>.
     */
    public void setDeadline(OffsetDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.task.deadline</code>.
     */
    public OffsetDateTime getDeadline() {
        return (OffsetDateTime) get(3);
    }

    /**
     * Setter for <code>public.task.status</code>.
     */
    public void setStatus(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.task.status</code>.
     */
    public Integer getStatus() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>public.task.estimated_time</code>.
     */
    public void setEstimatedTime(YearToSecond value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.task.estimated_time</code>.
     */
    public YearToSecond getEstimatedTime() {
        return (YearToSecond) get(5);
    }

    /**
     * Setter for <code>public.task.spent_time</code>.
     */
    public void setSpentTime(YearToSecond value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.task.spent_time</code>.
     */
    public YearToSecond getSpentTime() {
        return (YearToSecond) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TaskRecord
     */
    public TaskRecord() {
        super(Task.TASK);
    }

    /**
     * Create a detached, initialised TaskRecord
     */
    public TaskRecord(Integer taskId, Integer ownerId, String summary, OffsetDateTime deadline, Integer status, YearToSecond estimatedTime, YearToSecond spentTime) {
        super(Task.TASK);

        setTaskId(taskId);
        setOwnerId(ownerId);
        setSummary(summary);
        setDeadline(deadline);
        setStatus(status);
        setEstimatedTime(estimatedTime);
        setSpentTime(spentTime);
        resetChangedOnNotNull();
    }
}
