package ru.spbstu.ai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.spbstu.ai.component.*;

@Configuration
@PropertySource("classpath:bot.properties")
@ComponentScan(basePackages = "ru.spbstu.ai")
public class TelegramConfig {

    @Autowired
    Environment env;

    @Autowired
    StartCommand start;

    @Autowired
    TaskCommand task;

    @Autowired
    TaskCreateCommand createTask;

    @Autowired
    TaskDeadlineCommand taskDeadline;

    @Autowired
    TaskSpentTime taskSpentTime;

    @Autowired
    RecurringCreateCommand recurringCreate;

    @Autowired
    RecurringCommand recurring;

    @Bean
    public BotSession sessionStart(TelegramBotsLongPollingApplication botsApplication, TelegramBot bot) throws TelegramApiException {
        bot.register(start);
        bot.register(task);
        bot.register(createTask);
        bot.register(taskDeadline);
        bot.register(taskSpentTime);
        bot.register(recurringCreate);
        bot.register(recurring);
        return botsApplication.registerBot(env.getProperty("token"), bot);
    }

    @Bean
    public TelegramBotsLongPollingApplication application() {
        return new TelegramBotsLongPollingApplication();
    }

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(env.getProperty("token"));
    }
}
