package ru.spbstu.ai.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.spbstu.ai.service.UserService;

@Component
public class StartCommand extends BotCommand {

    @Autowired
    UserService users;

    public StartCommand() {
        super("start", "Register in the bot.");
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        var userDb = users.create(user.getId()).block();
        StringBuilder builder = new StringBuilder();
        builder.append("Welcome ").append(user.getUserName()).append("\n")
            .append("this bot will manage your tasks!");
        try {
            telegramClient.execute(new SendMessage(chat.getId().toString(), builder.toString()));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
