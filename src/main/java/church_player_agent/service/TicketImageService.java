package church_player_agent.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import com.google.zxing.EncodeHintType;
import java.util.EnumMap;
import java.util.Map;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

@Service
public class TicketImageService {
    private final String UPLOAD_DIR = "/var/www/tridarma-backend/uploads/tickets/";

    // Method ini HARUS ada
    public String generateAndSaveImage(String ticketCode, Long ticketId) throws Exception {
        String fileName = "ticket_" + ticketId + ".png";
        Path targetPath = Paths.get(UPLOAD_DIR + fileName);

        if (Files.exists(targetPath)) {
            return fileName;
        }

        // Logic generate QR
        InputStream is = getClass().getClassLoader().getResourceAsStream("templates/ticket_template.png");
        if (is == null) {
            throw new IllegalArgumentException("Template tidak ditemukan!");
        }
        BufferedImage background = ImageIO.read(is);
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 0);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(ticketCode, BarcodeFormat.QR_CODE, 800, 800, hints);
        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        Graphics2D g = background.createGraphics();
        g.drawImage(qrCodeImage, 610, 320, 800, 800, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(background, "png", baos);
        Files.write(targetPath, baos.toByteArray());
        
        return fileName;
    }
}