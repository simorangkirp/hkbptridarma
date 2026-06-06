package church_player_agent.repository;

import church_player_agent.entity.AnggotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnggotaRepository extends JpaRepository<AnggotaEntity, Long> {
}