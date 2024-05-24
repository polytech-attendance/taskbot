package ru.spbstu.ai.component;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.spbstu.ai.service.RecurringTaskService;
import ru.spbstu.ai.service.UserService;
import ru.spbstu.ai.utils.DurationParser;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

@Component
public class RecurringRescheduleCommand extends BotCommand {

    private final RecurringTaskService recurring;

    private final UserService users;

    public RecurringRescheduleCommand(RecurringTaskService recurring, UserService users) {
        super("reschedule", "Reschedule existing recurring task. Use next: /reschedule [Recurring Id] [hourly\\daily\\weekly\\monthly] [Deadline] (/reschedule 10 daily 2024-05-30T12:00:00Z");
        this.recurring = recurring;
        this.users = users;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        if (strings.length < 2) {
            sendMessageToChat(telegramClient, chat.getId(), "Invalid command format. Use: /reschedule <Recurring Id> <hourly\\daily\\weekly\\monthly> <Deadline> ");
            return;
        }
        int recurringId = Integer.parseInt(strings[0]);
        String periodString = strings[strings.length - 2];
        String deadlineString = strings[strings.length - 1];


        try {
            Instant deadline = Instant.parse(deadlineString);
            Duration period = DurationParser.parsePeriod(periodString);
            int telegramId = user.getId().intValue();
            users.getUser(telegramId)
                    .flatMap(foundUserId -> recurring.reschedule((int) foundUserId.userId(), recurringId, Instant.now(), period, deadline)
                            .doOnSuccess(value -> sendMessageToChat(telegramClient, chat.getId(), "Recurring task has been rescheduled."))
                            .doOnError(error -> sendMessageToChat(telegramClient, chat.getId(), "Recurring task rescheduled ended with error: " + error.getMessage()))
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
