import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import ru.spbstu.ai.config.DatabaseConfig;
import ru.spbstu.ai.config.TelegramConfig;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                DatabaseConfig.class, TelegramConfig.class
        );

        TelegramBotsApi api = context.getBean(TelegramBotsApi.class);
    }
}