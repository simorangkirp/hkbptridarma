import { subscribe } from "../core/state.js";

subscribe((state) => {
    const titleEl = document.getElementById("bottomCurrentTitle");
    const statusEl = document.getElementById("bottomCurrentStatus");

    if (titleEl) titleEl.innerText = state.player.currentTrack || "-";
    if (statusEl) statusEl.innerText = state.player.status || "-";
});