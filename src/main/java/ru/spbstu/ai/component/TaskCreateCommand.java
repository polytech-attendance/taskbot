package ru.spbstu.ai.component;

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
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

@Component
public class TaskCreateCommand extends BotCommand {

    private final TaskService tasks;

    private final UserService users;

    public TaskCreateCommand(TaskService tasks, UserService users) {
        super("task_create", "Creating new task. Use next: /task_create [Summary] [Deadline] [Estimated time] (/task_create Driving exam 2023-05-12T12:00:00Z 10 hours)");
        this.tasks = tasks;
        this.users = users;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        if (strings.length < 2) {
            sendMessageToChat(telegramClient, chat.getId(), "Invalid command format. Use: /task_create <Summary> <Deadline> <Estimated time>");
            return;
        }
        String estimatedTimeString = String.join(" ", Arrays.copyOfRange(strings, strings.length - 2, strings.length));
        String deadlineString = strings[strings.length - 3];
        String summary = String.join(" ", Arrays.copyOfRange(strings, 0, strings.length - 3));

        try {
            Instant deadline = Instant.parse(deadlineString);
            Duration estimatedTime = DurationParser.parse(estimatedTimeString);
            int telegramId = user.getId().intValue();
            users.getUser(telegramId)
                    .flatMap(foundUserId -> tasks.createTask((int) foundUserId.userId(), summary, deadline, estimatedTime)
                            .doOnSuccess(value -> sendMessageToChat(telegramClient, chat.getId(), "Task has been created."))
                            .doOnError(error -> sendMessageToChat(telegramClient, chat.getId(), "Task creation ended with error: " + error.getMessage()))
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
