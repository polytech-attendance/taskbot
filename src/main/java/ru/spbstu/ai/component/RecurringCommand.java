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
import ru.spbstu.ai.entity.RecurringTask;
import ru.spbstu.ai.entity.Task;
import ru.spbstu.ai.service.RecurringTaskService;
import ru.spbstu.ai.service.TaskService;
import ru.spbstu.ai.service.UserService;
import ru.spbstu.ai.utils.DurationParser;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class RecurringCommand extends BotCommand {

    @Autowired
    RecurringTaskService recurring;

    @Autowired
    UserService users;

    public RecurringCommand() {
        super("recurring", "Get recurring tasks in progress. Use next: /recurring");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        int telegramId = user.getId().intValue();
        users.getUser(telegramId)
                .flatMap(foundUserId -> {
                    return recurring.getRecurrings((int) foundUserId.userId())
                            .collectList()
                            .doOnSuccess(recurringList -> {
                                sendMessageToChat(telegramClient, chat.getId(), "Total amount of recurring task: " + "**" + recurringList.size() + "**");
                                for (RecurringTask recurringTask : recurringList) {
                                    sendTaskMessageWithButtons(telegramClient, chat.getId(), recurringTask);
                                }
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

    public void sendTaskMessageWithButtons(TelegramClient telegramClient, Long chatId, RecurringTask task) {
        SendMessage message = new SendMessage(chatId.toString(), task.toString());

        InlineKeyboardButton deleteButton = new InlineKeyboardButton("DELETE ❌");
        deleteButton.setCallbackData("recurring delete " + task.id());

        InlineKeyboardButton doneButton = new InlineKeyboardButton("DONE ✅");
        doneButton.setCallbackData("recurring done " + task.id());

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(doneButton);
        row1.add(deleteButton);


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
