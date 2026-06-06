package church_player_agent.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({ "status", "error", "message", "data" })
public class ApiResponse<T> {
    private int status;
    private boolean error;
    private String message;
    private T data;

    // Tambahkan method ini:
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .error(false)
                .message(message)
                .data(data)
                .build();
    }
}