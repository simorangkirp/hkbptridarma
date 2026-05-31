package church_player_agent;

import javafx.application.Platform;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "church_player_agent")
public class ChurchPlayerAgentApplication {

    public static void main(String[] args) {
        Platform.startup(() -> {});
        SpringApplication.run(ChurchPlayerAgentApplication.class, args);
    }
}