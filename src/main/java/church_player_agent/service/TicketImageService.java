package church_player_agent.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
    public byte[] generateTicketImage(String ticketCode) throws Exception {
        // 1. Load Background Template dengan pengecekan aman
        InputStream is = getClass().getClassLoader().getResourceAsStream("templates/ticket_template.png");
        if (is == null) {
            throw new IllegalArgumentException("File template tidak ditemukan di folder resources/templates/");
        }
        BufferedImage background = ImageIO.read(is);

        // 2. Setup QR Code
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 0);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        // REKOMENDASI: Langsung generate di ukuran besar agar tidak blur
        int qrSize = 800; 
        BitMatrix bitMatrix = qrCodeWriter.encode(
                ticketCode,
                BarcodeFormat.QR_CODE,
                qrSize, 
                qrSize, 
                hints
        );
        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // 3. Gabungkan (Overlay)
        Graphics2D g = background.createGraphics();
        
        // Karena QR sudah 800x800, di sini kita tidak perlu resize lagi (biarkan 800x800)
        int x = 610;
        int y = 320;
        g.drawImage(qrCodeImage, x, y, 800, 800, null);
        
        // Tambahkan rendering hint agar lebih tajam (opsional tapi bagus)
        g.dispose();

        // 4. Output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(background, "png", baos);
        return baos.toByteArray();
    }
}