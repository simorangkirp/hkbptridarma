package church_player_agent.service.impl;

import church_player_agent.service.PlayerService;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class JavaFxPlayerService implements PlayerService {

    private final AtomicReference<MediaPlayer> mediaPlayerRef = new AtomicReference<>();
    private volatile String status = "STOPPED";
    private volatile String currentTrack;
    private volatile Runnable onTrackEnded;
    private volatile double currentTimeSeconds = 0;
    private volatile double totalDurationSeconds = 0;
    private volatile double volume = 1.0;

    @Override
    public synchronized void play(String filePath) {
        stop();

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }

        Platform.runLater(() -> {
            Media media = new Media(file.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);

            player.setVolume(volume);

            player.setOnReady(() -> {
                System.out.println("Media ready: " + file.getAbsolutePath());
                Duration total = player.getTotalDuration();
                totalDurationSeconds = total != null ? total.toSeconds() : 0;
            });

            player.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                currentTimeSeconds = newTime != null ? newTime.toSeconds() : 0;
            });

            player.setOnPlaying(() -> status = "PLAYING");
            player.setOnPaused(() -> status = "PAUSED");
            player.setOnStopped(() -> status = "STOPPED");

            player.setOnEndOfMedia(() -> {
                status = "ENDED";
                currentTimeSeconds = totalDurationSeconds;
                System.out.println("JavaFX onEndOfMedia triggered for: " + file.getName());

                MediaPlayer current = mediaPlayerRef.getAndSet(null);
                if (current != null) {
                    current.stop();
                    current.dispose();
                }

                currentTrack = null;

                if (onTrackEnded != null) {
                    onTrackEnded.run();
                } else {
                    System.out.println("onTrackEnded callback is null");
                }
            });

            player.setOnError(() -> {
                status = "ERROR";
                System.err.println("MediaPlayer error: " + player.getError());
            });

            media.setOnError(() -> System.err.println("Media error: " + media.getError()));

            mediaPlayerRef.set(player);
            currentTrack = file.getName();
            currentTimeSeconds = 0;
            totalDurationSeconds = 0;
            player.play();
        });
    }

    @Override
    public synchronized void pause() {
        MediaPlayer player = mediaPlayerRef.get();
        if (player != null) {
            Platform.runLater(player::pause);
        }
    }

    @Override
    public synchronized void resume() {
        MediaPlayer player = mediaPlayerRef.get();
        if (player != null) {
            Platform.runLater(player::play);
        }
    }

    @Override
    public synchronized void stop() {
        MediaPlayer player = mediaPlayerRef.getAndSet(null);
        if (player != null) {
            Platform.runLater(() -> {
                player.stop();
                player.dispose();
            });
        }
        status = "STOPPED";
        currentTrack = null;
        currentTimeSeconds = 0;
        totalDurationSeconds = 0;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public String getCurrentTrack() {
        return currentTrack;
    }

    @Override
    public void setOnTrackEnded(Runnable callback) {
        System.out.println("onTrackEnded callback registered");
        this.onTrackEnded = callback;
    }

    @Override
    public double getCurrentTimeSeconds() {
        return currentTimeSeconds;
    }

    @Override
    public double getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    @Override
    public void seek(double seconds) {
        MediaPlayer player = mediaPlayerRef.get();
        if (player == null) {
            throw new IllegalStateException("No track is currently playing");
        }

        double maxSeek = totalDurationSeconds > 1 ? totalDurationSeconds - 0.5 : totalDurationSeconds;
        double safeSeconds = Math.max(0, Math.min(seconds, maxSeek));

        Platform.runLater(() -> player.seek(Duration.seconds(safeSeconds)));
        currentTimeSeconds = safeSeconds;
    }

    @Override
    public void setVolume(double volume) {
        double safeVolume = Math.max(0, Math.min(volume, 1));

        this.volume = safeVolume;

        MediaPlayer player = mediaPlayerRef.get();
        if (player != null) {
            Platform.runLater(() -> player.setVolume(safeVolume));
        }
    }

    @Override
    public double getVolume() {
        return volume;
    }
}