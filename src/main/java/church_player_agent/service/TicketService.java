package church_player_agent.service;

import church_player_agent.dto.request.TicketCreateRequest;
import church_player_agent.dto.request.TicketValidationRequest;
import church_player_agent.dto.response.TicketValidationResponse;
import church_player_agent.entity.TicketEntity;
import church_player_agent.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    // 1. LOGIK GENERATE TIKET BARU
    @Transactional
    public List<TicketEntity> generateTickets(TicketCreateRequest request) {
        List<TicketEntity> savedTickets = new ArrayList<>();

        // Loop sebanyak jumlah nama pengunjung yang didaftarkan oleh pembeli
        for (String attendeeName : request.getNames()) {
            TicketEntity ticket = new TicketEntity();

            // Generate Token Acak UUID (Contoh hasil:
            // "f47ac10b-58cc-4372-a567-0e02b2c3d479")
            String randomToken = UUID.randomUUID().toString();

            ticket.setTicketCode(randomToken);
            ticket.setIssuer(request.getIssuer());
            ticket.setName(attendeeName);
            ticket.setUsed(false);

            savedTickets.add(ticketRepository.save(ticket));
        }

        return savedTickets; // Mengembalikan list tiket yang sukses disimpan
    }

    // 2. LOGIK VALIDASI SCANNER (MATCHING KE DATABASE)
    @Transactional
    public TicketValidationResponse validateTicket(TicketValidationRequest request) {
        String code = request.getTicketCode();

        // Cari tiket di database berdasarkan string code dari QR
        Optional<TicketEntity> ticketOptional = ticketRepository.findByTicketCode(code);

        // Kasus A: Jika kode QR ngawur / tidak terdaftar
        if (ticketOptional.isEmpty()) {
            return new TicketValidationResponse(false, "Tiket tidak terdaftar di dalam sistem!");
        }

        TicketEntity ticket = ticketOptional.get();

        // Kasus B: Jika tiket terdaftar tapi statusnya sudah TRUE (sudah terpakai)
        if (ticket.isUsed()) {
            return new TicketValidationResponse(
                    false,
                    "Gagal! Tiket ini sudah pernah di-scan sebelumnya.",
                    ticket.getName(),
                    ticket.getIssuer());
        }

        // Kasus C: Sukses! Ubah status isUsed menjadi true agar tidak bisa
        // disalahgunakan lagi
        ticket.setUsed(true);
        ticketRepository.save(ticket); // Commit update ke database

        return new TicketValidationResponse(
                true,
                "Tiket Berhasil Diverifikasi!",
                ticket.getName(),
                ticket.getIssuer());
    }
}