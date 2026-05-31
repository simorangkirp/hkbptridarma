package church_player_agent.service;

import church_player_agent.model.QueueItem;

import java.io.IOException;
import java.util.List;

public interface QueueService {

    QueueItem addToQueue(Long trackId); // ✅ pakai DB

    List<QueueItem> getQueue();

    QueueItem playNext() throws IOException; // tetap dipakai player

    QueueItem getCurrentPlaying();

    void clearQueue();

    QueueItem removeFromQueue(String id);

    void skipCurrent();
}