package church_player_agent.dto.request;

import java.util.List;

public class TicketCreateRequest {
    private String issuer;
    private List<String> names; // Menerima banyak nama pengunjung sekaligus jika beli > 1

    // Getters and Setters
    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public List<String> getNames() { return names; }
    public void setNames(List<String> names) { this.names = names; }
}
