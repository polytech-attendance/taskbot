package ru.spbstu.ai.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.spbstu.ai.entity.TaskStatus;
import ru.spbstu.ai.service.RecurringTaskService;
import ru.spbstu.ai.service.TaskService;
import ru.spbstu.ai.utils.CallbackData;
import ru.spbstu.ai.utils.MarkupTask;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TelegramBot extends CommandLongPollingTelegramBot {

    private final TaskService tasks;

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final RecurringTaskService recurrings;

    // TODO remove this hack and use db
    private final ConcurrentHashMap<Integer, Long> usersChats;

    private final ScheduledExecutorService scheduler;

    private static final int BACKGROUND_TASK_PERIOD = 120;

    public TelegramBot(TelegramClient client,
                       @Value("${bot.name}") String botName,
                       TaskService tasks,
                       RecurringTaskService recurrings) {
        super(client, true, () -> botName);
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::backgroundTasks, 0, BACKGROUND_TASK_PERIOD, TimeUnit.SECONDS);
        usersChats = new ConcurrentHashMap<>();
        this.tasks = tasks;
        this.recurrings = recurrings;
    }

    private void backgroundTasks() {
        logger.info("Update background tasks.");
        resetRecurrings();
        checkForDeadlines();
        checkForUpdates();
    }

    private void resetRecurrings() {
        for (var entry : usersChats.entrySet()) {
            Integer telegramId = entry.getKey();
            Long chatId = entry.getValue();

            final Instant now = Instant.now();
            recurrings.getRecurrings(telegramId)
                    .filter(task -> {
                        Instant period = task.start().plus(task.period());
                        return task.status().equals(TaskStatus.DONE) &&
                               now.isAfter(period);
                    }).flatMap(task -> recurrings.markInProgress(telegramId, task.id()).thenReturn(task))
                    .subscribe(task -> {
                        SendMessage notify = new SendMessage(chatId.toString(), "Your recurring should be complete: " + task.summary());
                        try {
                            telegramClient.execute(notify);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }


    private void checkForDeadlines() {
        for (var entry : usersChats.entrySet()) {
            Integer telegramId = entry.getKey();
            Long chatId = entry.getValue();

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime plusWeek = now.plusWeeks(1);

            Instant plusDayInstant = now.toInstant(ZoneOffset.UTC);
            Instant plusWeekInstant = plusWeek.toInstant(ZoneOffset.UTC);

            tasks.getByDeadline(telegramId, plusDayInstant, plusWeekInstant)
                    .next()
                    .subscribe(task -> {
                        SendMessage notify = new SendMessage(chatId.toString(), "You should check /task. You have deadlines in next week.");
                        try {
                            telegramClient.execute(notify);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private void checkForUpdates() {
        for (Map.Entry<Integer, Long> entry : usersChats.entrySet()) {
            Integer telegramId = entry.getKey();
            Long chatId = entry.getValue();
            Instant now = Instant.now();
            recurrings.getRecurrings(telegramId).subscribe(task -> {
                Instant period = task.start().plus(task.period());
                if (!task.status().equals(TaskStatus.IN_PROGRESS) ||
                    !now.isAfter(period)) {
                    return;
                }

                SendMessage notify = new SendMessage(chatId.toString(), "Your recurring should be complete: " + task.summary());
                try {
                    telegramClient.execute(notify);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            logger.info("Sent text: {}", message_text);
            return;
        }
        if (!update.hasCallbackQuery()) {
            logger.debug("Callback query data does not start with 'done' or 'in_progress'.");
            return;
        }
        var callbackQueryData = update.getCallbackQuery().getData().split(" ");
        logger.info("Callback query: {}", update.getCallbackQuery().getData());

        int telegramUserId = update.getCallbackQuery().getFrom().getId().intValue();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        logger.debug("Callback query: {}", update.getCallbackQuery().getData());
        var mono = switch (CallbackData.parse(update.getCallbackQuery().getData())) {
            case CallbackData.RecurringDelete(int taskId) -> recurrings.deleteRecurring(telegramUserId, taskId)
                    .thenReturn(EditMessageText.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .text("Task has been deleted.")
                            .build());
            case CallbackData.RecurringDone(int taskId) -> recurrings.markDone(telegramUserId, taskId)
                    .then(recurrings.getById(telegramUserId, taskId))
                    .map(task -> EditMessageText.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .text(task.toHumanReadableString())
                            .build());
            case CallbackData.TaskDone(int taskId) ->
                    tasks.markDone(telegramUserId, taskId).then(tasks.getTaskById(telegramUserId, taskId))
                            .map(task -> EditMessageText.builder()
                                    .chatId(chatId)
                                    .messageId(messageId)
                                    .text(task.toHumanReadableString())
                                    .replyMarkup(MarkupTask.markupForTask(task))
                                    .build());
            case CallbackData.TaskInProgress(int taskId) -> tasks.markInProgress(telegramUserId, taskId)
                    .then(tasks.getTaskById(telegramUserId, taskId))
                    .map(task -> EditMessageText.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .text(task.toHumanReadableString())
                            .replyMarkup(MarkupTask.markupForTask(task))
                            .build());
        };
        mono.subscribe(editMessage -> {
            try {
                telegramClient.execute(editMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    public void processInvalidCommandUpdate(Update update) {
        super.processInvalidCommandUpdate(update);
    }

    @Override
    public boolean filter(Message message) {
        logger.info("Got message: {} from User: {} (id={})", message.getText(), message.getFrom().getUserName(), message.getFrom().getId());
        usersChats.put(message.getFrom().getId().intValue(), message.getChatId());
        return super.filter(message);
    }
}
