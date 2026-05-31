import { state } from "../core/state.js";
import { on } from "../core/events.js";

function formatTime(seconds) {
    if (!seconds) return "00:00";
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${String(mins).padStart(2, "0")}:${String(secs).padStart(2, "0")}`;
}

function renderBottomPlayer() {
    const player = state.player;
    if (!player) return;

    const titleEl = document.getElementById("bottomCurrentTitle");
    const statusEl = document.getElementById("bottomCurrentStatus");
    const currentTimeEl = document.getElementById("bottomCurrentTime");
    const totalTimeEl = document.getElementById("bottomTotalTime");
    const fillEl = document.getElementById("bottomProgressFill");
    const thumbEl = document.getElementById("progressThumb");
    const volumeEl = document.getElementById("bottomVolumeValue");
    const volumeSlider = document.getElementById("bottomVolumeSlider");

    const current = player.currentTime || 0;
    const total = player.duration || 0;
    const percent = total > 0 ? (current / total) * 100 : 0;

    if (titleEl) titleEl.innerText = player.currentTrack || "No track playing";
    if (statusEl) statusEl.innerText = player.status || "Stopped";
    if (currentTimeEl) currentTimeEl.innerText = formatTime(current);
    if (totalTimeEl) totalTimeEl.innerText = formatTime(total);

    if (fillEl) fillEl.style.width = percent + "%";
    if (thumbEl) thumbEl.style.left = percent + "%";

    if (volumeEl) volumeEl.innerText = Math.round((player.volume || 1) * 100) + "%";
    if (volumeSlider) volumeSlider.value = Math.round((player.volume || 1) * 100);
}

// Subscribe ke event
on("player:update", renderBottomPlayer);
on("player:play", renderBottomPlayer);
on("player:pause", renderBottomPlayer);

// pertama kali load
setTimeout(renderBottomPlayer, 500);