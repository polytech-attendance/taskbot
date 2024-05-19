package ru.spbstu.ai.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
@PropertySource("classpath:db.properties")
public class DatabaseConfig {

    @Autowired
    Environment env;

    DSLContext dslContext() {
        ConnectionFactory factory = ConnectionFactories.get(Objects.requireNonNull(env.getProperty("r2dbc_url")));
        return DSL.using(factory, SQLDialect.POSTGRES);
    }
}
