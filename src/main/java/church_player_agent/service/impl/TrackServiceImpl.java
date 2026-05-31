package church_player_agent.service.impl;

import church_player_agent.constant.TrackStatus;
import church_player_agent.dto.request.RegisterTrackRequest;
import church_player_agent.dto.response.TrackResponse;
import church_player_agent.entity.TrackEntity;
import church_player_agent.exception.DuplicateTrackException;
import church_player_agent.repository.TrackRepository;
import church_player_agent.service.TrackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;

    public TrackServiceImpl(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @Override
    @Transactional
    public void registerTrack(RegisterTrackRequest request) {
        boolean exists = trackRepository.existsByFileName(request.getFileName());
        if (exists) {
            throw new DuplicateTrackException("File sudah pernah dikirim: " + request.getFileName());
        }

        TrackEntity track = new TrackEntity();
        track.setFileName(request.getFileName());
        track.setFilePath(request.getFilePath());
        track.setStatus(TrackStatus.NOT_PLAYED);
        track.setCreatedAt(LocalDateTime.now());
        track.setPlayedAt(null);

        trackRepository.save(track);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackResponse> getIncomingTracks() {
        return trackRepository.findByStatusOrderByCreatedAtDesc(TrackStatus.NOT_PLAYED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackResponse> getPlayedTracks() {
        return trackRepository.findByStatusOrderByPlayedAtDesc(TrackStatus.PLAYED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void markAsPlayed(String fileName) {
        TrackEntity track = trackRepository.findByFileName(fileName)
                .orElseThrow(() -> new RuntimeException("Track not found: " + fileName));

        track.setStatus(TrackStatus.PLAYED);
        track.setPlayedAt(LocalDateTime.now());

        trackRepository.save(track);
    }

    private TrackResponse toResponse(TrackEntity entity) {
        return new TrackResponse(
                entity.getId(),
                entity.getFileName(),
                entity.getFilePath(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getPlayedAt());
    }
}