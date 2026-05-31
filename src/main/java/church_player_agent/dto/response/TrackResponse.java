package church_player_agent.dto.response;

import java.time.LocalDateTime;

public class TrackResponse {

    private Long id;
    private String fileName;
    private String filePath;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime playedAt;

    public TrackResponse() {
    }

    public TrackResponse(Long id, String fileName, String filePath, Integer status,
                         LocalDateTime createdAt, LocalDateTime playedAt) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.status = status;
        this.createdAt = createdAt;
        this.playedAt = playedAt;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public Integer getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }
}