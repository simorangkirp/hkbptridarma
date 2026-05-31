package church_player_agent.repository;

import church_player_agent.entity.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<TrackEntity, Long> {

    boolean existsByFileName(String fileName);

    Optional<TrackEntity> findByFileName(String fileName);

    List<TrackEntity> findByStatusOrderByCreatedAtDesc(Integer status);

    List<TrackEntity> findByStatusOrderByPlayedAtDesc(Integer status);
}