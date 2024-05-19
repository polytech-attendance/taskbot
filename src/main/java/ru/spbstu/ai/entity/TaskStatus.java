package ru.spbstu.ai.entity;

public enum TaskStatus {
    IN_PROGRESS,
    DONE;

    public static TaskStatus of(int databaseValue) {
        if (databaseValue == 0) {
            return IN_PROGRESS;
        } else {
            return DONE;
        }
    }
}
