package church_player_agent.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QueueItem {

    private String id; // 🔥 ubah ini
    private String fileName;
    private String absolutePath;
    private LocalDateTime queuedAt;
}