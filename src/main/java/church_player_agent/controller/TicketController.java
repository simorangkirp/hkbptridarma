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
        String baseUrl = "http://103.193.179.186:8080/api/v1/tickets";
        List<TicketResponse> responseList = createdTickets.stream().map(t -> new TicketResponse(
                t.getId(),
                t.getIsUsed(),
                t.getIssuer(),
                t.getName(),
                t.getTicketCode(),
                baseUrl + "/" + t.getId() + "/image" // URL Gambar otomatis terbentuk
        )).collect(Collectors.toList());

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
        // 1. Ambil data tiket berdasarkan ID (buat method di service untuk
        // getTicketById)
        TicketEntity ticket = ticketService.getTicketById(id);

        // 2. Generate gambar
        try {
            byte[] image = ticketImageService.generateTicketImage(ticket.getTicketCode());
            return ResponseEntity.ok().body(image);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}