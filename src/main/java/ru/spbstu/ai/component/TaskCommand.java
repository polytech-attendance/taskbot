package ru.spbstu.ai.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TaskCommand extends BotCommand {

    @Autowired
    TaskService tasks;

    @Autowired
    UserService users;

    public TaskCommand() {
        super("task", "Show all tasks.");
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
                            .collectList()
                            .doOnSuccess(taskList -> {
                                sendMessageToChat(telegramClient, chat.getId(), "Total amout of task: " + "**" + taskList.size() + "**");
                                for (Task task : taskList) {
                                    sendTaskMessageWithButtons(telegramClient, chat.getId(), task);
                                }
                            })
                            .doOnError(error -> sendMessageToChat(telegramClient, chat.getId(), "Some error via getting tasks: " + error.getMessage()));
                })
                .subscribe();


    }

    private void sendMessageToChat(TelegramClient telegramClient, Long chatId, String message) {
        try {
            SendMessage msg = new SendMessage(chatId.toString(), message);
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Error sending message to chat", e);
        }
    }

    public void sendTaskMessageWithButtons(TelegramClient telegramClient, Long chatId, Task task) {
        SendMessage message = new SendMessage(chatId.toString(), task.toHumanReadableString());

        InlineKeyboardButton doneButton = new InlineKeyboardButton("DONE ✅");
        doneButton.setCallbackData("done task " + task.id());

        // Emoji clocks
        InlineKeyboardButton inProgressButton = new InlineKeyboardButton("IN PROGRESS \uD83D\uDD53");
        inProgressButton.setCallbackData("in_progress task " + task.id());

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(doneButton);
        row1.add(inProgressButton);

        List<InlineKeyboardRow> rows = new ArrayList<>();
        rows.add(new InlineKeyboardRow(row1));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
        message.setReplyMarkup(markup);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
