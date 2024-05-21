package ru.spbstu.ai.component;

import org.bouncycastle.util.StringList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import reactor.core.publisher.Mono;
import ru.spbstu.ai.entity.Task;
import ru.spbstu.ai.service.TaskService;
import ru.spbstu.ai.service.UserService;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Component
public class CreateTaskCommand extends BotCommand {

    @Autowired
    TaskService tasks;

    @Autowired
    UserService users;

    public CreateTaskCommand() {
        super("create_task", "Creating new task. Use next: /create_task <Summary> <Deadline> (/create_task Driving exam 2023-05-12)");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        if (strings.length < 2) {
            sendMessageToChat(telegramClient, chat.getId(), "Invalid command format. Use: /create_task <Summary> <Deadline>");
            return;
        }
        String deadlineString = strings[strings.length - 1];
        String summary = String.join(" ", Arrays.copyOfRange(strings, 0, strings.length - 1));

        try {
            Instant deadline = Instant.parse(deadlineString);
            int telegramId = user.getId().intValue();
            users.getUser(telegramId)
                    .flatMap(foundUserId -> tasks.createTask((int) foundUserId.userId(), summary, deadline, Duration.ZERO)
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
