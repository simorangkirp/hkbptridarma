import { loadLayout } from "./core/loader.js";
import { router } from "./core/router.js";

import { loadPlayerStatus, play, pause } from "./modules/player.js";
import { playNext, skip } from "./modules/queue.js";
import { addToQueue, removeItem } from "./modules/tracks.js";
import { openLyrics, saveSong } from "./modules/lyrics.js";
import { searchSong } from "./modules/search.js";
import { toggleSidebar, showPanel } from "./modules/sidebar.js";

// Import UI (renderer)
import "./ui/bottomPlayerUI.js";

// REGISTER GLOBAL FUNCTIONS (untuk onclick HTML)
window.toggleSidebar = toggleSidebar;
window.showPanel = showPanel;
window.playNext = playNext;
window.skip = skip;
window.addToQueue = addToQueue;
window.removeItem = removeItem;
window.openLyrics = openLyrics;
window.searchSong = searchSong;
window.saveSong = saveSong;

// Play / Pause tombol bawah
window.togglePlayPause = async () => {
    const status = window.appState?.player?.status;

    if (status === "PLAYING") {
        await pause();
    } else {
        await play();
    }
};

document.addEventListener("DOMContentLoaded", async () => {
    // Load sidebar + bottom player
    await loadLayout();

    // Load page
    await router();

    // Load player pertama kali
    await loadPlayerStatus();

    // Polling player tiap 1 detik (seperti sistem lama)
    setInterval(loadPlayerStatus, 1000);
});