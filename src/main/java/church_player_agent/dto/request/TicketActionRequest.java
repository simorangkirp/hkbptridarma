package church_player_agent.dto.request;

import lombok.Data;

@Data
public class TicketActionRequest {
    private String ticketCode; // Menggunakan String karena UUID formatnya String
    private String reason;
}