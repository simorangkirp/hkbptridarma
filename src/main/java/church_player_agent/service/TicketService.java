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
    private final TicketImageService ticketImageService;

    public TicketService(TicketRepository ticketRepository, TicketImageService ticketImageService) {
        this.ticketRepository = ticketRepository;
        this.ticketImageService = ticketImageService;
    }

    @Transactional
    public List<TicketEntity> generateTickets(TicketCreateRequest request) {
        if (request.getData() == null || request.getData().isEmpty()) {
            return Collections.emptyList();
        }

        List<TicketEntity> ticketsToSave = new ArrayList<>();

        for (TicketCreateRequest.PassengerData passenger : request.getData()) {
            if (passenger.getNama() == null || passenger.getNama().trim().isEmpty()) {
                continue;
            }

            TicketEntity ticket = new TicketEntity();
            ticket.setIssuer(request.getIssuer());
            ticket.setName(passenger.getNama());
            ticket.setIsUsed(0);
            ticket.setTicketCode(UUID.randomUUID().toString()); // Pastikan UUID diset sebelum simpan

            ticketsToSave.add(ticket);
        }

        // 3. Simpan semua dulu ke database agar ID terbentuk
        List<TicketEntity> savedTickets = ticketRepository.saveAll(ticketsToSave);

        // 4. Loop hasil yang sudah di-save untuk generate gambar
        for (TicketEntity ticket : savedTickets) {
            try {
                // Generate gambar dan simpan path-nya
                String fileName = ticketImageService.generateAndSaveImage(ticket.getTicketCode(), ticket.getId());
                ticket.setImagePath(fileName);
            } catch (Exception e) {
                // Log error jika generate gagal
                System.err.println("Gagal generate gambar untuk tiket ID: " + ticket.getId());
            }
        }

        // 5. Update kembali database dengan path gambar
        return ticketRepository.saveAll(savedTickets);
    }

    @Transactional
    // Method Scan Ticket (Update ini di TicketService.java)
    public TicketEntity scanTicket(String ticketCode) {
        // Cari pakai method yang sudah kamu buat di Repository
        TicketEntity ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new RuntimeException("Tiket dengan kode " + ticketCode + " tidak ditemukan"));

        if (ticket.getIsUsed() == 1) {
            throw new RuntimeException("Tiket ini sudah pernah digunakan!");
        }

        ticket.setIsUsed(1);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteTicket(String ticketCode) {
        TicketEntity ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new RuntimeException("Tiket tidak ditemukan, gagal menghapus."));

        ticketRepository.delete(ticket);
    }

    @Transactional
    public TicketEntity getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tiket tidak ditemukan dengan ID: " + id));
    }
}