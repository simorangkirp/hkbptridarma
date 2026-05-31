package church_player_agent.service;

import church_player_agent.dto.request.RegisterTrackRequest;
import church_player_agent.dto.response.TrackResponse;

import java.util.List;

public interface TrackService {

    void registerTrack(RegisterTrackRequest request);

    List<TrackResponse> getIncomingTracks();

    List<TrackResponse> getPlayedTracks();

    void markAsPlayed(String fileName);
}