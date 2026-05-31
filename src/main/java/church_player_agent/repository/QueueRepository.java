package church_player_agent.repository;

import church_player_agent.entity.QueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<QueueEntity, Long> {

    List<QueueEntity> findAllByOrderByQueueOrderAsc();

    Optional<QueueEntity> findFirstByStatusOrderByQueueOrderAsc(Integer status);

    int countByStatus(Integer status);
}