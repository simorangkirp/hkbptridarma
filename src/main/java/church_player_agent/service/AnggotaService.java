package church_player_agent.service;

import church_player_agent.entity.AnggotaEntity;
import church_player_agent.repository.AnggotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnggotaService {
    @Autowired
    private AnggotaRepository anggotaRepository;

    public List<AnggotaEntity> getAllAnggota() {
        return anggotaRepository.findAll();
    }
}