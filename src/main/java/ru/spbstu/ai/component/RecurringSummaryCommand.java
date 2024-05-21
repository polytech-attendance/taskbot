package ru.spbstu.ai.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.spbstu.ai.service.RecurringTaskService;
import ru.spbstu.ai.service.TaskService;
import ru.spbstu.ai.service.UserService;
import ru.spbstu.ai.utils.DurationParser;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

@Component
public class RecurringSummaryCommand extends BotCommand {

    @Autowired
    RecurringTaskService recurring;

    @Autowired
    UserService users;

    public RecurringSummaryCommand() {
        super("recurring_summary", "Edit summary for existing recurring task. Use next: /recurring_summary <Recurring Id> <New summary> (/reschedule 10 Read big books");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        if (strings.length < 2) {
            sendMessageToChat(telegramClient, chat.getId(), "Invalid command format. Use: /recurring_summary <Recurring Id> <New summary>");
            return;
        }
        int recurringId = Integer.parseInt(strings[0]);
        String summary = String.join(" ", Arrays.copyOfRange(strings, 1, strings.length));


        try {
            int telegramId = user.getId().intValue();
            users.getUser(telegramId)
                    .flatMap(foundUserId -> recurring.setSummary((int) foundUserId.userId(), recurringId, summary)
                            .doOnSuccess(value -> sendMessageToChat(telegramClient, chat.getId(), "Recurring task has been updated."))
                            .doOnError(error -> sendMessageToChat(telegramClient, chat.getId(), "Recurring task updating ended with error: " + error.getMessage()))
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
