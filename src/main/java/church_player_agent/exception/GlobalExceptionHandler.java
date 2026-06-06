package church_player_agent.exception;

import church_player_agent.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralError(Exception e) {
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.NOT_ACCEPTABLE.value()) // 406
                .error(true)
                .message("Error: " + e.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }
}