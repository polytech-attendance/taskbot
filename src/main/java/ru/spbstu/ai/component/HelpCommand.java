package ru.spbstu.ai.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.spbstu.ai.service.UserService;

import java.util.Collection;
import java.util.List;

@Component
public class HelpCommand extends BotCommand {


    @Autowired
    UserService users;

    private List<IBotCommand> commands;

    @Autowired
    public HelpCommand(List<IBotCommand> commandList) {
        super("help", "Get all the commands this bot provides");
        this.commands = commandList;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        StringBuilder helpMessageBuilder = new StringBuilder("Help\n");
        helpMessageBuilder.append("These are the registered commands for this Bot:\n\n");

        for (IBotCommand botCommand : commands) {
            helpMessageBuilder.append(botCommand.toString()).append("\n\n");
        }



        SendMessage helpMessage = new SendMessage(chat.getId().toString(), helpMessageBuilder.toString());
        helpMessage.enableHtml(true);
        try {
            telegramClient.execute(helpMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
