package church_player_agent.service;

import church_player_agent.dto.request.LoginRequest;
import church_player_agent.dto.request.RegisterRequest;
import church_player_agent.entity.RefreshToken;
import church_player_agent.entity.User;
import church_player_agent.repository.RefreshTokenRepository;
import church_player_agent.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map; // Tambahkan ini
import java.util.UUID; // Tambahkan ini

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject passwordEncoder

    @Transactional
    public String register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        return "User berhasil didaftarkan!";
    }

    // Logika Login
    public Map<String, Object> login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshTokenString = UUID.randomUUID().toString();

        // 1. Cek apakah user sudah punya token sebelumnya
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken()); // Ambil yang lama, atau buat baru jika belum ada

        // 2. Update data token
        refreshTokenEntity.setToken(refreshTokenString);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));

        // 3. Simpan (ini akan meng-update jika sudah ada, atau insert baru jika belum)
        refreshTokenRepository.save(refreshTokenEntity);

        // ... sisa kode untuk format expiresIn tetap sama ...
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedExpiry = expiryTime.format(formatter);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshTokenString);
        response.put("expiresIn", formattedExpiry);

        return response;
    }

    // Logika Refresh
    public String refreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(token -> {
                    if (token.getExpiryDate().isBefore(Instant.now())) {
                        refreshTokenRepository.delete(token);
                        throw new RuntimeException("Refresh token kadaluarsa!");
                    }
                    return jwtService.generateToken(token.getUser().getUsername());
                })
                .orElseThrow(() -> new RuntimeException("Refresh token tidak valid!"));
    }

    @Transactional
    public void logout(String refreshToken) {
        // Cari token di database, jika ada maka hapus
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> refreshTokenRepository.delete(token));
    }
}