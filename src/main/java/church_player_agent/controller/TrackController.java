package church_player_agent.controller;

import church_player_agent.dto.request.RegisterTrackRequest;
import church_player_agent.dto.response.TrackResponse;
import church_player_agent.service.TrackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerTrack(@Valid @RequestBody RegisterTrackRequest request) {
        trackService.registerTrack(request);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", "Track registered successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/incoming")
    public ResponseEntity<Map<String, Object>> getIncomingTracks() {
        List<TrackResponse> data = trackService.getIncomingTracks();

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", "Incoming tracks fetched successfully");
        body.put("data", data);

        return ResponseEntity.ok(body);
    }

    @GetMapping("/played")
    public ResponseEntity<Map<String, Object>> getPlayedTracks() {
        List<TrackResponse> data = trackService.getPlayedTracks();

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", "Played tracks fetched successfully");
        body.put("data", data);

        return ResponseEntity.ok(body);
    }
}