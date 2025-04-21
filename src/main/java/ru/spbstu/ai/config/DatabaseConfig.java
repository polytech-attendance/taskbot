package ru.spbstu.ai.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
public class DatabaseConfig {   

    @Autowired
    Environment env;

    @Bean
    DSLContext dslContext() {
        String host = env.getProperty("POSTGRES_HOST");
        String port = env.getProperty("POSTGRES_PORT");
        String db = env.getProperty("POSTGRES_DB");
        String user = env.getProperty("POSTGRES_USER");
        String pass = env.getProperty("POSTGRES_PASSWORD");

        String url = String.format("r2dbc:postgresql://%s:%s@%s:%s/%s", user, pass, host, port, db);

        ConnectionFactory factory = ConnectionFactories.get(url);
        return DSL.using(factory, SQLDialect.POSTGRES);
    }
}
