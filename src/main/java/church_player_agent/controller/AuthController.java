package church_player_agent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import church_player_agent.dto.ApiResponse;
import church_player_agent.dto.request.LoginRequest;
import church_player_agent.dto.request.RefreshTokenRequest;
import church_player_agent.dto.request.RegisterRequest;
import church_player_agent.service.AuthService; // Pastikan path ini benar

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> data = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(data, "Login berhasil"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestBody RefreshTokenRequest request) {
        // 1. Panggil service
        String newToken = authService.refreshToken(request.getRefreshToken());

        // 2. Bungkus ke dalam map
        Map<String, String> data = new HashMap<>();
        data.put("accessToken", newToken);

        // 3. Return dengan ApiResponse
        return ResponseEntity.ok(ApiResponse.success(data, "Token berhasil diperbarui"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody RegisterRequest registerRequest) {

        // 2. Simpan hasil pesan dari service
        String message = authService.register(registerRequest);

        // 3. Kembalikan dengan ApiResponse
        // Tipe ini akan cocok dengan ResponseEntity<ApiResponse<Object>>
        return ResponseEntity.ok(ApiResponse.success(null, message));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(null, "Logout berhasil, sesi telah diakhiri"));
    }
}