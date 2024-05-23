package ru.spbstu.ai.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class SendMessageWithHtml {

    public static void sendMessage(TelegramClient telegramClient, Long chatId, String message) {
        try {
            SendMessage msg = new SendMessage(chatId.toString(), message);
            msg.enableHtml(true);
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Error sending message to chat", e);
        }
    }
}
