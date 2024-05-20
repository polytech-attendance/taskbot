import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.longpolling.BotSession;
import ru.spbstu.ai.config.DatabaseConfig;
import ru.spbstu.ai.config.TelegramConfig;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                DatabaseConfig.class, TelegramConfig.class
        );

        context.getBean(BotSession.class);
        Thread.currentThread().join();
    }
}