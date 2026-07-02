package church_player_agent.dto.response;

public class TicketResponse {
    private Long id;
    private int isUsed;
    private String issuer;
    private String name;
    private String ticketCode;
    private String ticketImg; // URL gambar

    // Constructor agar mudah mapping
    public TicketResponse(Long id, int isUsed, String issuer, String name, String ticketCode, String ticketImg) {
        this.id = id;
        this.isUsed = isUsed;
        this.issuer = issuer;
        this.name = name;
        this.ticketCode = ticketCode;
        this.ticketImg = ticketImg;
    }

    // Getters
    public Long getId() { return id; }
    public int getIsUsed() { return isUsed; }
    public String getIssuer() { return issuer; }
    public String getName() { return name; }
    public String getTicketCode() { return ticketCode; }
    public String getTicketImg() { return ticketImg; }
}