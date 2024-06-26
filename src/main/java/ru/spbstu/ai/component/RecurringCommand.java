package ru.spbstu.ai.component;

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
import ru.spbstu.ai.service.RecurringTaskService;
import ru.spbstu.ai.utils.CallbackData;
import ru.spbstu.ai.utils.SendMessageWithHtml;

import java.util.ArrayList;
import java.util.List;

@Component
public class RecurringCommand extends BotCommand {

    private final RecurringTaskService recurring;

    public RecurringCommand(RecurringTaskService recurring) {
        super("recurring", "Get recurring tasks in progress. Use next: /recurring");
        this.recurring = recurring;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        int telegramId = user.getId().intValue();
        recurring.getRecurrings(telegramId)
                .collectList()
                .doOnSuccess(recurringList -> {
                    SendMessageWithHtml.sendMessage(telegramClient, chat.getId(), "Total amount of recurring task: " + "<b>" + recurringList.size() + "</b>");
                    for (RecurringTask recurringTask : recurringList) {
                        sendTaskMessageWithButtons(telegramClient, chat.getId(), recurringTask);
                    }
                })
                .doOnError(error -> SendMessageWithHtml.sendMessage(telegramClient, chat.getId(), "Some error via getting tasks: " + error.getMessage()))
                .subscribe();
    }

    public void sendTaskMessageWithButtons(TelegramClient telegramClient, Long chatId, RecurringTask task) {
        SendMessage message = new SendMessage(chatId.toString(), task.toHumanReadableString());

        InlineKeyboardButton deleteButton = new InlineKeyboardButton("DELETE ❌");
        deleteButton.setCallbackData(new CallbackData.RecurringDelete(task.id()).data());

        InlineKeyboardButton doneButton = new InlineKeyboardButton("DONE ✅");
        doneButton.setCallbackData(new CallbackData.RecurringDone(task.id()).data());

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
