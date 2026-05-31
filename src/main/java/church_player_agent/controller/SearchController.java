package church_player_agent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class SearchController {

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String query) {
        try {
            String url = "https://api.deezer.com/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(url, String.class);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/lyrics")
    public ResponseEntity<?> lyrics(
            @RequestParam String artist,
            @RequestParam String title) {

        try {
            title = title.replaceAll("\\(.*?\\)", "").trim();

            String url = "https://api.lyrics.ovh/v1/"
                    + URLEncoder.encode(artist, StandardCharsets.UTF_8)
                    + "/"
                    + URLEncoder.encode(title, StandardCharsets.UTF_8);

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(url, String.class);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}