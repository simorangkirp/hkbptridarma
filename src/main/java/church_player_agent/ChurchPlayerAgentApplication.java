package church_player_agent;

import javafx.application.Platform;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Arrays;

@SpringBootApplication(scanBasePackages = "church_player_agent")
public class ChurchPlayerAgentApplication {

    public static void main(String[] args) {
        // 1. Paksa Headless Mode SEBELUM apapun terjadi
        System.setProperty("java.awt.headless", "true");
        System.out.println("====== SYSTEM: Headless Mode Activated ======");

        // 2. Gunakan cara yang lebih stabil untuk deteksi profil
        String profile = System.getProperty("spring.profiles.active");
        boolean isProd = (profile != null && profile.contains("prod")) ||
                Arrays.asList(args).contains("--spring.profiles.active=prod");

        if (!isProd) {
            System.out.println("====== RUNNING IN LOCAL MODE: Initializing JavaFX Platform ======");
            Platform.startup(() -> {
            });
        } else {
            System.out.println("====== RUNNING IN SERVER MODE: JavaFX Platform Skipped ======");
        }

        SpringApplication.run(ChurchPlayerAgentApplication.class, args);
    }
}