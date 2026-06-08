package church_player_agent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import church_player_agent.entity.RefreshToken;
import church_player_agent.entity.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    void deleteByUser(church_player_agent.entity.User user);
}