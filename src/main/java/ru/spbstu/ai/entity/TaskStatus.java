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

    public String toHumanReadableString() {
        if (this.equals(IN_PROGRESS)) {
            return "\uD83D\uDD53";
        }
        else {
            return "✅";
        }
    }
}
