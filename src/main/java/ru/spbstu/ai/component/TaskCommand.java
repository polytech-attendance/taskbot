package ru.spbstu.ai.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.spbstu.ai.entity.Task;
import ru.spbstu.ai.service.TaskService;
import ru.spbstu.ai.service.UserService;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Component
public class TaskCommand extends BotCommand {

    @Autowired
    TaskService tasks;

    @Autowired
    UserService users;

    public TaskCommand() {
        super("task", "Show all tasks. Or use /task <id>.");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        LocalDateTime monthLater = LocalDateTime.now().plusMonths(1);

        Instant monthAgoInstant = monthAgo.toInstant(ZoneOffset.UTC);
        Instant monthLaterInstant = monthLater.toInstant(ZoneOffset.UTC);

        /* Now we will be output tasks with delta [now - delta; now + delta], where delta = 1 month. */
        int telegramId = user.getId().intValue();
        users.getUser(telegramId)
                .flatMap(foundUserId -> {
                    return tasks.getByDeadline((int) foundUserId.userId(), monthAgoInstant, monthLaterInstant)
                            .collectList() // Собираем все задачи в список
                            .doOnSuccess(taskList -> {
                                StringBuilder tasksMessage = new StringBuilder("Your tasks:\n");
                                for (Task task : taskList) {
                                    tasksMessage.append(task.toString()).append("\n"); // Добавляем каждую задачу в сообщение
                                }
                                sendMessageToChat(telegramClient, chat.getId(), tasksMessage.toString()); // Отправляем сообщение с задачами
                            })
                            .doOnError(error -> sendMessageToChat(telegramClient, chat.getId(), "Some error via getting tasks: " + error.getMessage()));
                })
                .subscribe();


    }

    private void sendMessageToChat(TelegramClient telegramClient, Long chatId, String message) {
        try {
            telegramClient.execute(new SendMessage(chatId.toString(), message));
        } catch (TelegramApiException e) {
            throw new RuntimeException("Error sending message to chat", e);
        }
    }
}
