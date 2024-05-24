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

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Component
public class TaskDeadlineCommand extends BotCommand {

    private final TaskService tasks;

    private final UserService users;

    public TaskDeadlineCommand(TaskService tasks, UserService users) {
        super("task_deadline", "Change deadline for task. Use next: /task_deadline [Task_id] [Deadline] (/create_task 2023-05-12T12:00:00Z)");
        this.tasks = tasks;
        this.users = users;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        if (strings.length < 2) {
            sendMessageToChat(telegramClient, chat.getId(), "Invalid command format. Use: /task_deadline <Task_id> <Deadline>");
            return;
        }
        String deadlineString = strings[strings.length - 1];
        int task_id = Integer.parseInt(strings[0]);

        try {
            Instant deadline = Instant.parse(deadlineString);
            int telegramId = user.getId().intValue();
            users.getUser(telegramId)
                    .flatMap(foundUserId -> tasks.setDeadline((int) foundUserId.userId(), task_id, deadline)
                            .doOnSuccess(value -> sendMessageToChat(telegramClient, chat.getId(), "Task deadline has changed."))
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
