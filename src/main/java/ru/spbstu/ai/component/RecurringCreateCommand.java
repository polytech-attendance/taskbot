package ru.spbstu.ai.component;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.spbstu.ai.service.RecurringTaskService;
import ru.spbstu.ai.utils.DurationParser;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

@Component
public class RecurringCreateCommand extends BotCommand {

    private final RecurringTaskService recurring;

    public RecurringCreateCommand(RecurringTaskService recurring) {
        super("recurring_create", "Creating new recurring task. Use next: /recurring_create [Summary] [hourly\\daily\\weekly\\monthly] [Deadline] (/recurring_create Reading book daily 2024-05-24T12:00:00Z");
        this.recurring = recurring;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        if (strings.length < 3) {
            sendMessageToChat(telegramClient, chat.getId(), "Invalid command format. Use: /recurring_create <Summary> <hourly\\daily\\weekly\\monthly> <Deadline>");
            return;
        }
        String periodString = strings[strings.length - 2];
        String deadlineString = strings[strings.length - 1];
        String summary = String.join(" ", Arrays.copyOfRange(strings, 0, strings.length - 2));

        try {
            Instant deadline = Instant.parse(deadlineString);
            Duration period = DurationParser.parsePeriod(periodString);
            int telegramId = user.getId().intValue();
            recurring.createRecurring(telegramId, summary, Instant.now(), period, deadline)
                    .doOnSuccess(value -> sendMessageToChat(telegramClient, chat.getId(), "Recurring task has been created."))
                    .doOnError(error -> sendMessageToChat(telegramClient, chat.getId(), "Recurring task creation ended with error: " + error.getMessage())).subscribe();

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
