package church_player_agent.repository;

import church_player_agent.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    // Dipakai saat Scanner: Mencari tiket berdasarkan string token UUID
    Optional<TicketEntity> findByTicketCode(String ticketCode);
}