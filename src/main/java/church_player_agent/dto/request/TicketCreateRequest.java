package church_player_agent.dto.request;

import java.util.List;

public class TicketCreateRequest {
    private String issuer;
    private List<PassengerData> data;

    // Getters dan Setters
    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public List<PassengerData> getData() {
        return data;
    }

    public void setData(List<PassengerData> data) {
        this.data = data;
    }

    public static class PassengerData {
        private String nama;

        // Getters dan Setters
        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }
    }
}
