package church_player_agent.controller;

import church_player_agent.dto.PlayRequest;
import church_player_agent.dto.SeekRequest;
import church_player_agent.dto.VolumeRequest;
import church_player_agent.model.Track;
import church_player_agent.service.FileScannerService;
import church_player_agent.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerController {

    private final FileScannerService fileScannerService;
    private final PlayerService playerService;

    @GetMapping("/tracks")
    public ResponseEntity<List<Track>> getTracks() throws IOException {
        return ResponseEntity.ok(fileScannerService.scanIncoming());
    }

    @PostMapping("/play")
    public ResponseEntity<Map<String, String>> play(@Valid @RequestBody PlayRequest request) {
        playerService.play(request.getPath());
        return ResponseEntity.ok(Map.of("message", "Track started"));
    }

    @PostMapping("/play-debug")
    public ResponseEntity<Map<String, String>> playDebug(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("received", String.valueOf(body.get("path"))));
    }

    @PostMapping("/pause")
    public ResponseEntity<Map<String, String>> pause() {
        playerService.pause();
        return ResponseEntity.ok(Map.of("message", "Track paused"));
    }

    @PostMapping("/stop")
    public ResponseEntity<Map<String, String>> stop() {
        playerService.stop();
        return ResponseEntity.ok(Map.of("message", "Track stopped"));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of(
                "status", playerService.getStatus(),
                "currentTrack", String.valueOf(playerService.getCurrentTrack()),
                "currentTimeSeconds", playerService.getCurrentTimeSeconds(),
                "totalDurationSeconds", playerService.getTotalDurationSeconds(),
                "volume", playerService.getVolume()));
    }

    @PostMapping("/seek")
    public ResponseEntity<Map<String, String>> seek(@RequestBody SeekRequest request) {
        playerService.seek(request.getSeconds());
        return ResponseEntity.ok(Map.of("message", "Seek updated"));
    }

    @PostMapping("/resume")
    public ResponseEntity<Map<String, String>> resume() {
        playerService.resume();
        return ResponseEntity.ok(Map.of("message", "Track resumed"));
    }

    @PostMapping("/volume")
    public ResponseEntity<Map<String, String>> setVolume(@RequestBody VolumeRequest request) {
        playerService.setVolume(request.getVolume());
        return ResponseEntity.ok(Map.of("message", "Volume updated"));
    }
}