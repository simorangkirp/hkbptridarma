package church_player_agent.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Track {

    private String fileName;
    private String absolutePath;
    private long size;
}