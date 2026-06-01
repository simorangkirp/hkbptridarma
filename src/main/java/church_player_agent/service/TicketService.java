package church_player_agent.service;

import church_player_agent.dto.request.TicketCreateRequest;
import church_player_agent.entity.TicketEntity;
import church_player_agent.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public List<TicketEntity> generateTickets(TicketCreateRequest request) {
        // Validasi pencegahan jika body data kosong/null agar tidak memicu
        // NullPointerException
        if (request.getData() == null || request.getData().isEmpty()) {
            return Collections.emptyList();
        }

        List<TicketEntity> ticketsToSave = new ArrayList<>();

        for (TicketCreateRequest.PassengerData passenger : request.getData()) {
            // Skip jika ada object array yang namanya dikirim kosong oleh frontend
            if (passenger.getNama() == null || passenger.getNama().trim().isEmpty()) {
                continue;
            }

            TicketEntity ticket = new TicketEntity();
            ticket.setIssuer(request.getIssuer());
            ticket.setName(passenger.getNama());
            ticket.setIsUsed(0);

            ticketsToSave.add(ticket);
        }

        return ticketRepository.saveAll(ticketsToSave);
    }
}