package church_player_agent.controller;

import church_player_agent.dto.ApiResponse;
import church_player_agent.entity.AnggotaEntity;
import church_player_agent.service.AnggotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/anggota")
public class AnggotaController {

    @Autowired
    private AnggotaService anggotaService;

    // Ubah ke POST
    @PostMapping
    public ResponseEntity<ApiResponse<List<AnggotaEntity>>> getAll() {
        List<AnggotaEntity> data = anggotaService.getAllAnggota();

        ApiResponse<List<AnggotaEntity>> response = ApiResponse.<List<AnggotaEntity>>builder()
                .status(200)
                .error(false)
                .message("Berhasil Ambil Data.")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}