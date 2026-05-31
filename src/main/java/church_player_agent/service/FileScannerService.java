package church_player_agent.service;

import church_player_agent.config.AppConfig;
import church_player_agent.model.Track;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FileScannerService {

    private final AppConfig appConfig;

    public List<Track> scanIncoming() throws IOException {
        Path incomingPath = Path.of(appConfig.getIncomingDir());
        Files.createDirectories(incomingPath);

        try (Stream<Path> stream = Files.list(incomingPath)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(this::isSupported)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase(Locale.ROOT)))
                    .map(this::toTrack)
                    .toList();
        }
    }

    private boolean isSupported(Path path) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);

        return appConfig.getSupportedExtensions().stream()
                .anyMatch(ext -> fileName.endsWith("." + ext.toLowerCase(Locale.ROOT)));
    }

    private Track toTrack(Path path) {
        try {
            return Track.builder()
                    .fileName(path.getFileName().toString())
                    .absolutePath(path.toAbsolutePath().toString())
                    .size(Files.size(path))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file info: " + path, e);
        }
    }
}