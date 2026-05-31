import { state, setState } from "../core/state.js";
import { apiPost, apiGet } from "../core/api.js";
import { emit } from "../core/events.js";

export async function loadPlayerStatus() {
    const data = await apiGet("/api/player/status");

    setState({
        player: {
            status: data.status,
            currentTrack: data.currentTrack,
            currentTime: data.currentTimeSeconds,
            duration: data.totalDurationSeconds,
            volume: data.volume
        }
    });

    emit("player:update"); // ← TAMBAH INI
}

export async function play() {
    await apiPost("/api/player/resume");
    await loadPlayerStatus();
    emit("player:play");
}

export async function pause() {
    await apiPost("/api/player/pause");
    await loadPlayerStatus();
    emit("player:pause");
}