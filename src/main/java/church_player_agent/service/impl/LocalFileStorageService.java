package church_player_agent.service.impl;

import church_player_agent.config.AppConfig;
import church_player_agent.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final AppConfig appConfig;

    @Override
    public void moveToPlayed(String filePath) {
        Path source = Path.of(filePath);
        Path playedDir = Path.of(appConfig.getPlayedDir());

        try {
            Files.createDirectories(playedDir);
            Path target = playedDir.resolve(source.getFileName());

            System.out.println("Copying file to played: " + target);

            // 1. COPY dulu
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            // 2. coba delete dengan retry
            int maxAttempts = 10;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    Files.delete(source);
                    System.out.println("Deleted original file: " + source);
                    return;
                } catch (IOException e) {
                    if (attempt == maxAttempts) {
                        throw e;
                    }

                    System.out.println("Delete attempt " + attempt + " failed, retrying...");
                    Thread.sleep(500);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to move file to played folder", e);
        }
    }
}