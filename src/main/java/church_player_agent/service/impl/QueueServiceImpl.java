package church_player_agent.service.impl;

import church_player_agent.entity.QueueEntity;
import church_player_agent.entity.TrackEntity;
import church_player_agent.model.QueueItem;
import church_player_agent.repository.QueueRepository;
import church_player_agent.repository.TrackRepository;
import church_player_agent.service.PlayerService;
import church_player_agent.service.QueueService;
import church_player_agent.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    private final QueueRepository queueRepository;
    private final TrackRepository trackRepository;

    private final PlayerService playerService;
    private final TrackService trackService;

    @Override
    public QueueItem addToQueue(Long trackId) {
        if (trackId == null) {
            throw new RuntimeException("trackId is required");
        }

        TrackEntity track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        int nextOrder = queueRepository.findAll().size() + 1;

        QueueEntity entity = new QueueEntity();
        entity.setTrackId(trackId);
        entity.setQueueOrder(nextOrder);
        entity.setStatus(0);
        entity.setCreatedAt(LocalDateTime.now());

        queueRepository.save(entity);

        return mapToQueueItem(entity, track);
    }

    @Override
    public List<QueueItem> getQueue() {
        return queueRepository.findAllByOrderByQueueOrderAsc()
                .stream()
                .map(q -> {
                    TrackEntity track = trackRepository.findById(q.getTrackId()).orElse(null);
                    return mapToQueueItem(q, track);
                })
                .toList();
    }

    @Override
    public QueueItem playNext() {
        // 1. stop player dulu
        playerService.stop();

        // 2. cari yang lagi playing → set jadi done
        QueueEntity current = queueRepository
                .findFirstByStatusOrderByQueueOrderAsc(1)
                .orElse(null);

        if (current != null) {
            current.setStatus(2);
            queueRepository.save(current);

            TrackEntity currentTrack = trackRepository.findById(current.getTrackId()).orElse(null);
            if (currentTrack != null) {
                trackService.markAsPlayed(currentTrack.getFileName());
            }
        }

        // 3. ambil queue berikutnya
        QueueEntity next = queueRepository
                .findFirstByStatusOrderByQueueOrderAsc(0)
                .orElseThrow(() -> new RuntimeException("Queue is empty"));

        TrackEntity track = trackRepository.findById(next.getTrackId())
                .orElseThrow(() -> new RuntimeException("Track not found"));

        // 4. play ke player
        playerService.play(track.getFilePath());

        // 5. update status
        next.setStatus(1);
        queueRepository.save(next);

        return mapToQueueItem(next, track);
    }

    @Override
    public QueueItem getCurrentPlaying() {
        QueueEntity current = queueRepository
                .findFirstByStatusOrderByQueueOrderAsc(1)
                .orElse(null);

        if (current == null)
            return null;

        TrackEntity track = trackRepository.findById(current.getTrackId()).orElse(null);

        return mapToQueueItem(current, track);
    }

    @Override
    public void clearQueue() {
        queueRepository.deleteAll();
    }

    @Override
    public QueueItem removeFromQueue(String id) {
        if (id == null) {
            throw new RuntimeException("id is required");
        }

        queueRepository.deleteById(Long.parseLong(id));
        return null;
    }

    @Override
    public void skipCurrent() {
        playerService.stop();

        QueueEntity current = queueRepository
                .findFirstByStatusOrderByQueueOrderAsc(1)
                .orElse(null);

        if (current != null) {
            current.setStatus(2);
            queueRepository.save(current);

            TrackEntity track = trackRepository.findById(current.getTrackId()).orElse(null);
            if (track != null) {
                trackService.markAsPlayed(track.getFileName());
            }
        }
    }

    // 🔥 helper biar clean & reusable
    private QueueItem mapToQueueItem(QueueEntity entity, TrackEntity track) {
        return QueueItem.builder()
                .id(entity.getId().toString())
                .fileName(track != null ? track.getFileName() : "-")
                .absolutePath(track != null ? track.getFilePath() : null)
                .queuedAt(entity.getCreatedAt())
                .build();
    }
}