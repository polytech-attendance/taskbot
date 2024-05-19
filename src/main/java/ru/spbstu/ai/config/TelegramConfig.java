package ru.spbstu.ai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:bot.properties")
public class TelegramConfig {

    @Autowired
    Environment env;

}
