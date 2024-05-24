package ru.spbstu.ai.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.spbstu.ai.service.TaskService;
import ru.spbstu.ai.service.UserService;
import ru.spbstu.ai.utils.DurationParser;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

@Component
public class TaskSpentTime extends BotCommand {

    @Autowired
    TaskService tasks;

    @Autowired
    UserService users;

    public TaskSpentTime() {
        super("task_spenttime", "Add spent time for task. Use next: /task_spenttime [Task_id] [Duration] (/create_task 3 hours)");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        if (strings.length < 2) {
            sendMessageToChat(telegramClient, chat.getId(), "Invalid command format. Use: /task_deadline <Task_id> <Deadline>");
            return;
        }
        String durationString = String.join(" ", Arrays.copyOfRange(strings, 1, strings.length));
        int task_id = Integer.parseInt(strings[0]);

        try {
            Duration spentTime = DurationParser.parse(durationString);
            int telegramId = user.getId().intValue();
            users.getUser(telegramId)
                    .flatMap(foundUserId -> tasks.addSpentTime((int) foundUserId.userId(), task_id, spentTime)
                            .doOnSuccess(value -> sendMessageToChat(telegramClient, chat.getId(), "Task spent time increase on " + durationString))
                            .doOnError(error -> sendMessageToChat(telegramClient, chat.getId(), "Task update ended with error: " + error.getMessage()))
                            .then())
                    .subscribe();

        } catch (DateTimeParseException e) {
            sendMessageToChat(telegramClient, chat.getId(), "Invalid date format for deadline. Use ISO format (yyyy-MM-ddTHH:mm:ssZ)");
        }
    }


    private void sendMessageToChat(TelegramClient telegramClient, Long chatId, String message) {
        try {
            telegramClient.execute(new SendMessage(chatId.toString(), message));
        } catch (TelegramApiException e) {
            throw new RuntimeException("Error sending message to chat", e);
        }
    }
}
