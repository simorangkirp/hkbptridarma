package church_player_agent.controller;

import church_player_agent.dto.ApiResponse;
import church_player_agent.dto.request.TicketActionRequest;
import church_player_agent.dto.request.TicketCreateRequest;
import church_player_agent.entity.TicketEntity;
import church_player_agent.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@CrossOrigin(origins = "*") // Penting: Biar aplikasi Flutter (Android/iOS) tidak kena blokir CORS
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Generate Tiket
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<TicketEntity>>> createTickets(@RequestBody TicketCreateRequest request) {
        List<TicketEntity> createdTickets = ticketService.generateTickets(request);
        return ResponseEntity.ok(ApiResponse.success(createdTickets, "Tiket berhasil digenerate"));
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
}