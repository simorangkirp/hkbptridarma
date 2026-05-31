package church_player_agent.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RegisterTrackRequest {

    @NotBlank(message = "fileName is required")
    private String fileName;

    @NotBlank(message = "filePath is required")
    private String filePath;

    public RegisterTrackRequest() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}