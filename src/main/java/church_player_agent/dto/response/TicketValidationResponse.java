package church_player_agent.dto.response;

public class TicketValidationResponse {
    private boolean valid;
    private String message;
    private String name;
    private String issuer;

    public TicketValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public TicketValidationResponse(boolean valid, String message, String name, String issuer) {
        this.valid = valid;
        this.message = message;
        this.name = name;
        this.issuer = issuer;
    }

    // Getters
    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getIssuer() {
        return issuer;
    }
}