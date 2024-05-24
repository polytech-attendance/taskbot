package ru.spbstu.ai.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import reactor.core.publisher.Mono;
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
        signUp(user); // No message to reply similar for both cases: user exists, and not.

        StringBuilder builder = new StringBuilder();
        builder.append("Welcome ").append(user.getUserName()).append("\n")
            .append("this bot will manage your tasks!");
        try {
            telegramClient.execute(new SendMessage(chat.getId().toString(), builder.toString()));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void signUp(User user) {
        users.getUser(user.getId())
                .hasElement()  // Check if user exists
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.empty();
                    } else {
                        return users.create(user.getId())
                                .doOnNext(newUser -> {
                                    // TODO: Create log info output, about creating user
                                });
                    }
                }).block();
    }
}
