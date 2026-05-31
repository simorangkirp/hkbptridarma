package church_player_agent.controller;

import church_player_agent.dto.AddToQueueRequest;
import church_player_agent.model.QueueItem;
import church_player_agent.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/add")
    public ResponseEntity<QueueItem> addToQueue(@RequestBody AddToQueueRequest request) {
        return ResponseEntity.ok(queueService.addToQueue(request.getTrackId()));
    }

    @GetMapping
    public ResponseEntity<List<QueueItem>> getQueue() {
        return ResponseEntity.ok(queueService.getQueue());
    }

    @PostMapping("/play-next")
    public ResponseEntity<QueueItem> playNext() throws IOException {
        return ResponseEntity.ok(queueService.playNext());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearQueue() {
        queueService.clearQueue();
        return ResponseEntity.ok(Map.of("message", "Queue cleared"));
    }

    @GetMapping("/current")
    public ResponseEntity<QueueItem> getCurrentPlaying() {
        return ResponseEntity.ok(queueService.getCurrentPlaying());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<QueueItem> remove(@PathVariable String id) {
        return ResponseEntity.ok(queueService.removeFromQueue(id));
    }

    @PostMapping("/skip")
    public ResponseEntity<Map<String, String>> skip() {
        queueService.skipCurrent();
        return ResponseEntity.ok(Map.of("message", "Skipped current track"));
    }
}