package ru.spbstu.ai.utils;

sealed public interface CallbackData permits
        CallbackData.RecurringDelete,
        CallbackData.RecurringDone,
        CallbackData.TaskDone,
        CallbackData.TaskInProgress {
    record RecurringDelete(int taskId) implements CallbackData {

        @Override
        public String data() {
            return "recurring delete " + taskId;
        }
    }

    record RecurringDone(int taskId) implements CallbackData {

        @Override
        public String data() {
            return "recurring done " + taskId;
        }
    }

    record TaskDone(int taskId) implements CallbackData {

        @Override
        public String data() {
            return "done task " + taskId;
        }
    }

    record TaskInProgress(int taskId) implements CallbackData {

        @Override
        public String data() {
            return "in_progress task " + taskId;
        }
    }

    String data();

    static CallbackData parse(String data) {
        var callbackQueryData = data.split(" ");
        if (callbackQueryData.length == 3 && "recurring".equals(callbackQueryData[0]) && "delete".equals(callbackQueryData[1])) {
            return new RecurringDelete(Integer.parseInt(callbackQueryData[2]));
        }
        if (callbackQueryData.length == 3 && "recurring".equals(callbackQueryData[0]) && "done".equals(callbackQueryData[1])) {
            return new RecurringDone(Integer.parseInt(callbackQueryData[2]));
        }
        if (callbackQueryData.length == 3 && "done".equals(callbackQueryData[0]) && "task".equals(callbackQueryData[1])) {
            return new TaskDone(Integer.parseInt(callbackQueryData[2]));
        }
        if (callbackQueryData.length == 3 && "in_progress".equals(callbackQueryData[0]) && "task".equals(callbackQueryData[1])) {
            return new TaskInProgress(Integer.parseInt(callbackQueryData[2]));
        }
        throw new IllegalArgumentException("Could not parse: \"" + data + "\"");
    }
}
