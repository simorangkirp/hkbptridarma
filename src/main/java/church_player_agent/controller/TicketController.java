package church_player_agent.controller;

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

    @PostMapping("/generate")
    public ResponseEntity<List<TicketEntity>> createTickets(@RequestBody TicketCreateRequest request) {
        List<TicketEntity> createdTickets = ticketService.generateTickets(request);
        return ResponseEntity.ok(createdTickets);
    }
}