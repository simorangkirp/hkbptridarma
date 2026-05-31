package church_player_agent.controller;

import church_player_agent.dto.request.TicketCreateRequest;
import church_player_agent.dto.request.TicketValidationRequest;
import church_player_agent.dto.response.TicketValidationResponse;
import church_player_agent.entity.TicketEntity;
import church_player_agent.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@CrossOrigin(origins = "*") // Penting: Biar aplikasi Flutter (Android/iOS) tidak kena blokir CORS
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // Endpoint A: Membuat Tiket Baru (Dipakai saat proses pembelian)
    @PostMapping("/generate")
    public ResponseEntity<List<TicketEntity>> createTicket(@RequestBody TicketCreateRequest request) {
        List<TicketEntity> newTickets = ticketService.generateTickets(request);
        return ResponseEntity.ok(newTickets);
    }

    // Endpoint B: Memvalidasi Tiket (Dipakai saat Scanner Flutter melakukan scan
    // QR)
    @PostMapping("/validate")
    public ResponseEntity<TicketValidationResponse> verifyTicket(@RequestBody TicketValidationRequest request) {
        TicketValidationResponse response = ticketService.validateTicket(request);
        return ResponseEntity.ok(response);
    }
}