package church_player_agent.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    private String waQrBase64 = null;
    private boolean waReady = false;

    @PostMapping("/wa-qr")
    public Map<String, String> receiveQr(@RequestBody Map<String, String> body) {
        this.waQrBase64 = body.get("qr");
        this.waReady = false;
        return Map.of("message", "QR received");
    }

    @PostMapping("/wa-ready")
    public Map<String, String> waReady() {
        this.waReady = true;
        this.waQrBase64 = null;
        return Map.of("message", "WA ready");
    }

    @PostMapping("/wa-disconnected")
    public Map<String, String> waDisconnected() {
        this.waReady = false;
        return Map.of("message", "WA disconnected");
    }

    @GetMapping("/wa-qr")
    public Map<String, Object> getQr() {
        return Map.of(
                "ready", waReady,
                "qr", waQrBase64);
    }
}