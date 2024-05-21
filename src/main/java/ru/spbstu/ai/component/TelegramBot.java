package ru.spbstu.ai.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.spbstu.ai.service.TaskService;
import ru.spbstu.ai.service.UserService;

@Component
public class TelegramBot extends CommandLongPollingTelegramBot {

    @Autowired
    TaskService tasks;

    @Autowired
    UserService users;

    public TelegramBot(TelegramClient client, @Value("${bot.name}") String botName) {
        super(client, true, () -> botName);
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            System.out.println("Sended text: " + message_text);
        } else if (update.hasCallbackQuery()) {
            var callbackQueryData = update.getCallbackQuery().getData().split( " ");
            System.out.println("Callback query: " + update.getCallbackQuery().getData());

            Long userId = update.getCallbackQuery().getFrom().getId();
            users.getUser(userId)
                    .map(user -> user.userId())
                    .doOnSuccess(ownerId -> {
                        System.out.println("Owner ID: " + ownerId);
                        // DONE, IN_PROGRESS
                        if (callbackQueryData.length >= 1 && (callbackQueryData[0].equals("done") || callbackQueryData[0].equals("in_progress"))) {
                            if (callbackQueryData.length >= 3) {
                                String secondElement = callbackQueryData[1];
                                String thirdElement = callbackQueryData[2];

                                System.out.println("Second element: " + secondElement);
                                System.out.println("Third element: " + thirdElement);

                                var id = Integer.parseInt(thirdElement);
                                var type = secondElement;

                                if (type.equals("task")) {
                                    if ((callbackQueryData[0].equals("done"))) {
                                        tasks.markDone(ownerId.intValue(), id).subscribe();
                                    }
                                    else
                                    {
                                        tasks.markInProgress(ownerId.intValue(), id).subscribe();
                                    }
                                }
                            } else {
                                System.out.println("Callback query data does not contain enough elements.");
                            }
                        }
                    })
                    .subscribe();



            } else {
                System.out.println("Callback query data does not start with 'done' or 'in_progress'.");
            }

        }


    @Override
    public void processInvalidCommandUpdate(Update update) {
        super.processInvalidCommandUpdate(update);
    }

    @Override
    public boolean filter(Message message) {
        return super.filter(message);
    }
}
