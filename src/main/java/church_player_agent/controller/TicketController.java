package church_player_agent.controller;

import church_player_agent.dto.ApiResponse;
import church_player_agent.dto.request.TicketActionRequest;
import church_player_agent.dto.request.TicketCreateRequest;
import church_player_agent.dto.response.TicketResponse;
import church_player_agent.entity.TicketEntity;
import church_player_agent.service.TicketImageService;
import church_player_agent.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path; // Tambahkan ini!
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tickets")
@CrossOrigin(origins = "*") // Penting: Biar aplikasi Flutter (Android/iOS) tidak kena blokir CORS
public class TicketController {

    private final TicketService ticketService;
    private final TicketImageService ticketImageService;

    public TicketController(TicketService ticketService, TicketImageService ticketImageService) {
        this.ticketService = ticketService;
        this.ticketImageService = ticketImageService;
    }

    // Generate Tiket
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> createTickets(@RequestBody TicketCreateRequest request) {
        // 1. Generate tiket via service
        List<TicketEntity> createdTickets = ticketService.generateTickets(request);

        // 2. Mapping Entity -> DTO dengan URL gambar
        String baseUrl = "http://103.193.179.186/tickets/images";

        List<TicketResponse> responseList = createdTickets.stream().map(t -> new TicketResponse(
                t.getId(),
                t.getIsUsed(),
                t.getIssuer(),
                t.getName(),
                t.getTicketCode(),
                // Gunakan t.getId() untuk mengambil ID dari objek tiket
                baseUrl + "/ticket_" + t.getId() + ".png")).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responseList, "Tiket berhasil digenerate"));
    }

    @PostMapping("/scan")
    public ResponseEntity<ApiResponse<TicketEntity>> scanTicket(@RequestBody TicketActionRequest request) {
        TicketEntity updatedTicket = ticketService.scanTicket(request.getTicketCode());
        return ResponseEntity.ok(ApiResponse.success(updatedTicket, "Tiket berhasil discan"));
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@RequestBody TicketActionRequest request) {
        ticketService.deleteTicket(request.getTicketCode());
        return ResponseEntity.ok(ApiResponse.success(null, "Tiket berhasil dihapus"));
    }

    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getTicketImage(@PathVariable Long id) {
        String fileName = "ticket_" + id + ".png";
        Path path = Paths.get("/var/www/tridarma-backend/uploads/tickets/" + fileName);

        // Cek apakah file ada
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        // Baca file langsung dari disk (Sangat Cepat!)
        try {
            byte[] image = Files.readAllBytes(path);
            return ResponseEntity.ok()
                    .header("Cache-Control", "max-age=86400") // Cache 24 jam
                    .body(image);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}