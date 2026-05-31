package church_player_agent.service;

public interface PlayerService {

    void play(String filePath);

    void pause();

    void stop();

    String getStatus();

    String getCurrentTrack();

    void setOnTrackEnded(Runnable callback);

    double getCurrentTimeSeconds();

    double getTotalDurationSeconds();

    void seek(double seconds);

    void resume();

    void setVolume(double volume);

    double getVolume();
}