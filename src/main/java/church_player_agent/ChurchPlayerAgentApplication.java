package church_player_agent;

import javafx.application.Platform;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Arrays;

@SpringBootApplication(scanBasePackages = "church_player_agent")
public class ChurchPlayerAgentApplication {

    public static void main(String[] args) {
        // Cek apakah ada parameter profile 'prod' yang dimasukkan saat running
        boolean isProd = Arrays.asList(args).contains("--spring.profiles.active=prod") 
                         || "prod".equals(System.getProperty("spring.profiles.active"));

        if (!isProd) {
            // Hanya jalankan JavaFX Toolkit jika BUKAN di server (di laptop lokal)
            System.out.println("====== RUNNING IN LOCAL MODE: Initializing JavaFX Platform ======");
            Platform.startup(() -> {});
        } else {
            // Jika di server, paksa Java berjalan dalam mode tanpa layar (Headless)
            System.out.println("====== RUNNING IN SERVER MODE: Activating Headless Mode ======");
            System.setProperty("java.awt.headless", "true");
        }

        SpringApplication.run(ChurchPlayerAgentApplication.class, args);
    }
}