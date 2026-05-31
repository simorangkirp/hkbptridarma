const BASE_URL = "http://localhost:8087";

let playerState = {
    status: "STOPPED",
    currentTrack: null,
    currentTimeSeconds: 0,
    totalDurationSeconds: 0,
    volume: 1
};

let isDragging = false;
let dragPreviewSeconds = 0;
let liveTimer = null;
let refreshTimer = null;

let lastTracksHash = "";
let lastQueueHash = "";
let lastPlayedHash = "";
let lastCurrentHash = "";

let playerRefreshTimer = null;
let listRefreshTimer = null;

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function setError(message) {
    const el = document.getElementById("error");
    if (el) {
        el.innerText = message || "";
    }
}

async function safeJson(res) {
    const text = await res.text();
    if (!text) return null;

    try {
        return JSON.parse(text);
    } catch {
        return text;
    }
}

function formatTime(seconds) {
    if (seconds == null || isNaN(seconds) || seconds < 0) {
        return "00:00";
    }

    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${String(mins).padStart(2, "0")}:${String(secs).padStart(2, "0")}`;
}

function getDisplayCurrentTime() {
    if (isDragging) {
        return dragPreviewSeconds;
    }
    return Number(playerState.currentTimeSeconds ?? 0);
}

function updateBottomPlayer() {
    const titleEl = document.getElementById("bottomCurrentTitle");
    const statusEl = document.getElementById("bottomCurrentStatus");
    const currentTimeEl = document.getElementById("bottomCurrentTime");
    const totalTimeEl = document.getElementById("bottomTotalTime");
    const progressFillEl = document.getElementById("bottomProgressFill");
    const progressThumbEl = document.getElementById("progressThumb"); // TAMBAH INI
    const volumeEl = document.getElementById("bottomVolumeValue");
    const volumeSliderEl = document.getElementById("bottomVolumeSlider");
    const toggleBtn = document.getElementById("bottomToggleBtn");

    const current = getDisplayCurrentTime();
    const total = Number(playerState.totalDurationSeconds ?? 0);
    const percent = total > 0 ? Math.min((current / total) * 100, 100) : 0;
    const volumePercent = Math.round(Number(playerState.volume ?? 1) * 100);

    if (titleEl) {
        titleEl.innerText = playerState.currentTrack || "No track playing";
    }

    if (statusEl) {
        statusEl.innerText = playerState.status || "Stopped";
    }

    if (currentTimeEl) {
        currentTimeEl.innerText = formatTime(current);
    }

    if (totalTimeEl) {
        totalTimeEl.innerText = formatTime(total);
    }

    if (progressFillEl) {
        progressFillEl.style.width = `${percent}%`;
    }

    if (progressThumbEl) { // TAMBAH INI
        progressThumbEl.style.left = `${percent}%`;
    }

    if (volumeEl) {
        volumeEl.innerText = `${volumePercent}%`;
    }

    if (volumeSliderEl) {
        volumeSliderEl.value = volumePercent;
    }

    if (toggleBtn) {
        if (playerState.status === "PLAYING") {
            toggleBtn.innerText = "⏸";
            toggleBtn.disabled = false;
        } else if (playerState.status === "PAUSED") {
            toggleBtn.innerText = "▶";
            toggleBtn.disabled = false;
        } else {
            toggleBtn.innerText = "▶";
            toggleBtn.disabled = true;
        }
    }
}

function toggleSidebar(forceExpand = false) {
    const sidebar = document.getElementById("sidebar");
    const appShell = document.getElementById("appShell");
    const logo = document.getElementById("brandLogo");
    const toggle = document.getElementById("sidebarToggle");

    if (forceExpand) {
        sidebar.classList.remove("minimized");
        appShell.classList.remove("minimized");
    } else {
        sidebar.classList.toggle("minimized");
        appShell.classList.toggle("minimized");
    }

    if (sidebar.classList.contains("minimized")) {
        logo.innerHTML = "☰";
        toggle.style.display = "none";
        logo.style.cursor = "pointer";
        logo.onclick = () => toggleSidebar(true);
    } else {
        logo.innerHTML = "♫";
        toggle.style.display = "block";
        logo.style.cursor = "default";
        logo.onclick = null;
    }
}

function updateProgressUI(percent, currentSeconds) {
    const fill = document.querySelector(".progress-fill");
    const thumb = document.querySelector(".progress-thumb");
    const currentTimeLabel = document.getElementById("currentTimeLabel");
    const totalTimeLabel = document.getElementById("totalTimeLabel");

    if (fill) {
        fill.style.width = `${percent}%`;
    }

    if (thumb) {
        thumb.style.left = `${percent}%`;
    }

    if (currentTimeLabel) {
        currentTimeLabel.innerText = formatTime(currentSeconds);
    }

    if (totalTimeLabel) {
        totalTimeLabel.innerText = formatTime(playerState.totalDurationSeconds ?? 0);
    }

    updateBottomPlayer();
}

function showPanel(panel) {
    document.getElementById("dashboardPanel").style.display = "none";
    document.getElementById("settingsPanel").style.display = "none";
    document.getElementById("searchPanel").style.display = "none";
    document.getElementById("lyricsPanel").style.display = "none";

    if (panel === "dashboard") {
        document.getElementById("dashboardPanel").style.display = "block";
    }

    if (panel === "settings") {
        document.getElementById("settingsPanel").style.display = "block";
        loadWABotStatus();
    }

    if (panel === "search") {
        document.getElementById("searchPanel").style.display = "block";
    }

    if (panel === "lyrics") {
        document.getElementById("lyricsPanel").style.display = "block";
    }
}

async function saveSong() {
    const artist = document.getElementById("artistInput").value;
    const title = document.getElementById("titleInput").value;
    const lyrics = document.getElementById("lyricsEditor").value;

    await fetch(`${BASE_URL}/api/songs`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            artist: artist,
            title: title,
            lyrics: lyrics
        })
    });

    alert("Song saved!");
}

async function searchSong(query) {
    if (!query || query.length < 2) {
        document.getElementById("searchResultsGrid").innerHTML = "";
        document.getElementById("seeAllBtn").style.display = "none";
        return;
    }

    // CLEAR GRID DULU
    document.getElementById("searchResultsGrid").innerHTML = "";

    const res = await fetch(`${BASE_URL}/api/search?query=${encodeURIComponent(query)}`);
    const data = await res.json();

    allSearchResults = data.data || [];

    renderGrid(allSearchResults.slice(0, 8));

    document.getElementById("seeAllBtn").style.display =
        allSearchResults.length > 8 ? "block" : "none";
}

async function selectSong(artist, title) {
    document.getElementById("artistInput").value = artist;
    document.getElementById("titleInput").value = title;

    const res = await fetch(`${BASE_URL}/api/lyrics?artist=${artist}&title=${title}`);
    const data = await res.json();

    document.getElementById("lyricsEditor").value = data.lyrics || "";
}

function syncProgressUI() {
    const current = getDisplayCurrentTime();
    const total = Number(playerState.totalDurationSeconds ?? 0);
    const percent = total > 0 ? Math.min((current / total) * 100, 100) : 0;
    updateProgressUI(percent, current);
}

function updateVolumeUI(volume) {
    const slider = document.getElementById("volumeSlider");
    const value = document.getElementById("volumeValue");
    const percent = Math.round(Number(volume ?? 1) * 100);

    if (slider) {
        slider.value = percent;
    }

    if (value) {
        value.innerText = `${percent}%`;
    }

    updateBottomPlayer();
}

function updatePlayPauseButton() {
    const topBtn = document.getElementById("togglePlayPauseBtn");

    if (topBtn) {
        if (playerState.status === "PLAYING") {
            topBtn.innerText = "⏸ Pause";
            topBtn.disabled = false;
        } else if (playerState.status === "PAUSED") {
            topBtn.innerText = "▶ Resume";
            topBtn.disabled = false;
        } else if (playerState.status === "LOADING") {
            topBtn.innerText = "⏳ Loading";
            topBtn.disabled = true;
        } else {
            topBtn.innerText = "⏸ Pause";
            topBtn.disabled = true;
        }
    }

    updateBottomPlayer();
}

function updateStatusMeta() {
    const statusText = document.getElementById("statusText");
    const currentTrackText = document.getElementById("currentTrackText");

    if (statusText) {
        statusText.innerText = playerState.status ?? "-";
    }

    if (currentTrackText) {
        currentTrackText.innerText = playerState.currentTrack ?? "-";
    }

    updatePlayPauseButton();
    updateBottomPlayer();
}


function setupVolumeInteraction() {
    const slider = document.getElementById("volumeSlider");
    if (!slider) return;

    slider.oninput = () => {
        const volume = Number(slider.value) / 100;
        updateVolumeUI(volume);
    };

    slider.onchange = async () => {
        const volume = Number(slider.value) / 100;
        await setVolume(volume);
    };
}

function setupBottomVolumeInteraction() {
    const slider = document.getElementById("bottomVolumeSlider");
    if (!slider) return;

    slider.oninput = () => {
        const volume = Number(slider.value) / 100;
        const value = document.getElementById("bottomVolumeValue");
        if (value) {
            value.innerText = `${Math.round(volume * 100)}%`;
        }
    };

    slider.onchange = async () => {
        const volume = Number(slider.value) / 100;
        await setVolume(volume);
    };
}

function setupProgressInteraction() {
    const track = document.getElementById("progressTrack");
    const thumb = document.getElementById("progressThumb");

    if (!track || !thumb) return;

    const calculateFromClientX = (clientX) => {
        const rect = track.getBoundingClientRect();
        let ratio = (clientX - rect.left) / rect.width;
        ratio = Math.max(0, Math.min(ratio, 1));

        const total = Number(playerState.totalDurationSeconds ?? 0);
        const seconds = ratio * total;
        const percent = ratio * 100;

        return { ratio, seconds, percent };
    };

    track.onclick = async (e) => {
        if (isDragging) return;
        if (!playerState.totalDurationSeconds) return;

        const { seconds } = calculateFromClientX(e.clientX);
        await seekTo(seconds);
    };

    thumb.onmousedown = (e) => {
        if (!playerState.totalDurationSeconds) return;

        e.preventDefault();
        e.stopPropagation();
        isDragging = true;

        const onMove = (moveEvent) => {
            const { seconds, percent } = calculateFromClientX(moveEvent.clientX);
            dragPreviewSeconds = seconds;
            updateProgressUI(percent, dragPreviewSeconds);
        };

        const onUp = async (upEvent) => {
            document.removeEventListener("mousemove", onMove);
            document.removeEventListener("mouseup", onUp);

            const { seconds } = calculateFromClientX(upEvent.clientX);
            dragPreviewSeconds = seconds;
            isDragging = false;

            await seekTo(dragPreviewSeconds);
        };

        document.addEventListener("mousemove", onMove);
        document.addEventListener("mouseup", onUp);
    };
}

async function seekTo(seconds) {
    try {
        const res = await fetch(`${BASE_URL}/api/player/seek`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ seconds })
        });

        const data = await safeJson(res);

        if (!res.ok) {
            throw new Error(data?.message || "Failed to seek");
        }

        playerState.currentTimeSeconds = seconds;
        syncProgressUI();
    } catch (err) {
        setError(`Error seeking: ${err.message}`);
    }
}

function startLiveTimer() {
    if (liveTimer) {
        clearInterval(liveTimer);
    }

    let lastTick = performance.now();

    liveTimer = setInterval(() => {
        if (isDragging) {
            lastTick = performance.now();
            return;
        }

        const now = performance.now();
        const deltaSeconds = (now - lastTick) / 1000;
        lastTick = now;

        if (playerState.status === "PLAYING" && playerState.totalDurationSeconds > 0) {
            playerState.currentTimeSeconds += deltaSeconds;

            if (playerState.currentTimeSeconds > playerState.totalDurationSeconds) {
                playerState.currentTimeSeconds = playerState.totalDurationSeconds;
            }

            syncProgressUI();
        }
    }, 100);
}

async function pauseTrack() {
    try {
        const res = await fetch(`${BASE_URL}/api/player/pause`, {
            method: "POST"
        });

        const data = await safeJson(res);

        if (!res.ok) {
            throw new Error(data?.message || "Failed to pause");
        }

        playerState.status = "PAUSED";
        updateStatusMeta();
    } catch (err) {
        setError(`Error pausing: ${err.message}`);
    }
}

async function resumeTrack() {
    try {
        const res = await fetch(`${BASE_URL}/api/player/resume`, {
            method: "POST"
        });

        const data = await safeJson(res);

        if (!res.ok) {
            throw new Error(data?.message || "Failed to resume");
        }

        playerState.status = "PLAYING";
        updateStatusMeta();
    } catch (err) {
        setError(`Error resuming: ${err.message}`);
    }
}

async function togglePlayPause() {
    if (playerState.status === "PLAYING") {
        await pauseTrack();
    } else if (playerState.status === "PAUSED") {
        await resumeTrack();
    }
}

async function setVolume(volume) {
    try {
        const res = await fetch(`${BASE_URL}/api/player/volume`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ volume })
        });

        const data = await safeJson(res);

        if (!res.ok) {
            throw new Error(data?.message || "Failed to set volume");
        }

        playerState.volume = volume;
        updateVolumeUI(volume);
    } catch (err) {
        setError(`Error setting volume: ${err.message}`);
    }
}

async function loadCurrent() {
    try {
        const res = await fetch(`${BASE_URL}/api/queue/current`);
        const data = await safeJson(res);
        const el = document.getElementById("current");

        if (!el) return;

        if (!res.ok || !data || data === "null") {
            const emptyHtml = "<span class='muted'>No track playing</span>";
            if (el.innerHTML !== emptyHtml) {
                el.innerHTML = emptyHtml;
            }
            lastCurrentHash = "empty";
            return;
        }

        const normalized = {
            id: data.id ?? "",
            fileName: data.fileName ?? "-"
        };

        const newHash = JSON.stringify(normalized);
        if (newHash === lastCurrentHash) return;
        lastCurrentHash = newHash;

        el.innerHTML = `
            <div class="card">
                <b>${escapeHtml(data.fileName ?? "-")}</b>
                <div class="small">${escapeHtml(String(data.id ?? ""))}</div>
            </div>
        `;
    } catch (err) {
        const el = document.getElementById("current");
        if (el) {
            el.innerHTML = "<span class='muted'>Cannot load current track</span>";
        }
        setError(`Error loading current: ${err.message}`);
    }
}

async function loadStatus() {
    try {
        const res = await fetch(`${BASE_URL}/api/player/status`);
        const data = await safeJson(res);

        if (!res.ok || !data) {
            setError("Failed to load player status");
            return;
        }

        playerState = {
            status: data.status ?? "STOPPED",
            currentTrack: data.currentTrack ?? null,
            currentTimeSeconds: Number(data.currentTimeSeconds ?? 0),
            totalDurationSeconds: Number(data.totalDurationSeconds ?? 0),
            volume: Number(data.volume ?? 1)
        };

        updateStatusMeta();
        syncProgressUI();
        updateVolumeUI(playerState.volume);
        updateBottomPlayer();
    } catch (err) {
        setError(`Error loading status: ${err.message}`);
    }
}

async function loadQueue() {
    try {
        const res = await fetch(`${BASE_URL}/api/queue`);
        const data = await safeJson(res);
        const el = document.getElementById("queue");

        if (!el) return;

        if (!res.ok || !Array.isArray(data) || data.length === 0) {
            const emptyHtml = "<span class='muted'>Queue empty</span>";
            if (el.innerHTML !== emptyHtml) {
                el.innerHTML = emptyHtml;
            }
            lastQueueHash = "empty";
            return;
        }

        const normalized = data.map(item => ({
            id: item.id,
            fileName: item.fileName
        }));

        const newHash = JSON.stringify(normalized);
        if (newHash === lastQueueHash) return;
        lastQueueHash = newHash;

        el.innerHTML = data.map(item => `
            <div class="card queue-row-card">
                <div class="queue-row-main">
                    <div class="queue-title-only">${escapeHtml((item.fileName || "").replace(/\.[^/.]+$/, ""))}</div>
                </div>
                <div class="queue-row-action">
                    <button onclick="removeItem('${item.id}')">❌ Remove</button>
                </div>
            </div>
        `).join("");
    } catch (err) {
        const el = document.getElementById("queue");
        if (el) {
            el.innerHTML = "<span class='muted'>Cannot load queue</span>";
        }
        setError(`Error loading queue: ${err.message}`);
    }
}

async function loadTracks() {
    try {
        const res = await fetch(`${BASE_URL}/api/tracks/incoming`);
        const result = await safeJson(res);
        const el = document.getElementById("tracks");

        if (!el) return;

        const tracks = Array.isArray(result) ? result : (result?.data ?? []);

        if (!res.ok || !Array.isArray(tracks) || tracks.length === 0) {
            const emptyHtml = "<span class='muted'>No tracks in database</span>";
            if (el.innerHTML !== emptyHtml) {
                el.innerHTML = emptyHtml;
            }
            return;
        }

        el.innerHTML = tracks.map(track => `
            <div class="card track-row-card">
                <div class="track-row-main">
                    <div class="track-title-inline">${escapeHtml(removeExtension(track.fileName))}</div>
                    <div class="track-path-inline">${escapeHtml(track.filePath)}</div>
                </div>
                <div class="track-row-action">
                    <button onclick="addToQueue(${track.id})">➕ Add to Queue</button>
                </div>
            </div>
        `).join("");
    } catch (err) {
        const el = document.getElementById("tracks");
        if (el) {
            el.innerHTML = "<span class='muted'>Cannot load tracks</span>";
        }
        setError(`Error loading tracks: ${err.message}`);
    }
}

async function loadWABotStatus() {
    try {
        const res = await fetch(`${BASE_URL}/api/system/wa-qr`);
        const data = await res.json();

        const statusEl = document.getElementById("waBotStatus");
        const qrEl = document.getElementById("waQrContainer");

        if (data.ready) {
            statusEl.innerHTML = "<b style='color:#256d0e'>WA Bot READY</b>";
            qrEl.innerHTML = "";
        } else if (data.qr) {
            statusEl.innerHTML = "<b style='color:#f8ee0e'>Scan QR to login WhatsApp</b>";
            qrEl.innerHTML = `<img src="${data.qr}" width="220" />`;
        } else {
            statusEl.innerHTML = "<b style='color:#ed4516'>WA Bot not running</b>";
            qrEl.innerHTML = "";
        }
    } catch (err) {
        console.error("Failed load WA status", err);
    }
}

async function addToQueue(trackId) {
    try {
        if (!trackId) {
            throw new Error("trackId tidak valid");
        }

        const response = await fetch(`${BASE_URL}/api/queue/add`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                trackId: trackId
            })
        });

        const result = await safeJson(response);

        if (!response.ok) {
            throw new Error(result?.message || "Failed to add queue");
        }

        await loadQueue();
    } catch (error) {
        console.error("Error addToQueue:", error);
        alert(error.message);
    }
}

let allSearchResults = [];

async function searchSong(query) {
    if (!query || query.length < 2) {
        document.getElementById("searchResultsGrid").innerHTML = "";
        document.getElementById("seeAllBtn").style.display = "none";
        return;
    }

    try {
        const res = await fetch(`${BASE_URL}/api/search?query=${encodeURIComponent(query)}`);
        const data = await res.json();

        allSearchResults = data.data || [];

        renderGrid(allSearchResults.slice(0, 8));

        document.getElementById("seeAllBtn").style.display =
            allSearchResults.length > 8 ? "block" : "none";

    } catch (err) {
        console.error("Search error:", err);
    }
}

function renderGrid(list) {
    let html = "";

    list.forEach(song => {
        const cover = song.album.cover_medium;
        const artist = song.artist.name.replace(/'/g, "");
        const title = song.title.replace(/'/g, "");
        const duration = formatDuration(song.duration);

        html += `
                <div class="song-tile">
                    <img src="${cover}">

                    <div class="song-info">
                        <div class="song-title">
                            <div class="title-inner">${title}</div>
                        </div>
                        <div class="song-artist">${artist}</div>
                        <div class="song-duration">${duration}</div>
                    </div>

                    <div class="song-actions">
                        <i data-lucide="play" class="icon-btn" onclick="playPreview('${title}')"></i>
                        <i data-lucide="file-text" class="icon-btn" onclick="openLyrics('${artist}', '${title}', '${cover}')"></i>
                    </div>
                </div>
            `;
    });

    document.getElementById("searchResultsGrid").innerHTML = html;
    lucide.createIcons();
    setupMarquee(); // ← penting
}

async function openLyrics(artist, title, cover) {
    showPanel("lyrics");

    // Set UI
    document.getElementById("lyricsTitle").innerText = title;
    document.getElementById("lyricsArtist").innerText = artist;
    document.getElementById("lyricsCover").src = cover;

    document.getElementById("lyricsEditor").value = "Loading lyrics...";

    try {
        const res = await fetch(`${BASE_URL}/api/lyrics?artist=${encodeURIComponent(artist)}&title=${encodeURIComponent(title)}`);
        const data = await res.json();

        document.getElementById("lyricsEditor").value =
            data.lyrics || "Lyrics not found. You can paste lyrics manually.";
    } catch (err) {
        document.getElementById("lyricsEditor").value =
            "Failed to load lyrics. You can paste lyrics manually.";
    }
}

function setupMarquee() {
    document.querySelectorAll(".song-title").forEach(el => {
        const inner = el.querySelector(".title-inner");

        if (inner.classList.contains("marquee")) return;

        if (inner.scrollWidth > el.clientWidth) {
            inner.innerHTML = inner.innerText + "   " + inner.innerText;
            inner.classList.add("marquee");
        }
    });
}

function startMarquee(el) {
    if (el.scrollWidth > el.clientWidth) {
        el.classList.add("marquee-active");
    }
}

function stopMarquee(el) {
    el.classList.remove("marquee-active");
}

function showAllResults() {
    renderGrid(allSearchResults);
}

function formatDuration(seconds) {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
}

function playPreview(title) {
    console.log("Play preview:", title);
}

async function loadPlayedHistory() {
    try {
        const res = await fetch(`${BASE_URL}/api/tracks/played`);
        const result = await safeJson(res);
        const el = document.getElementById("status");

        if (!el) return;

        const tracks = Array.isArray(result) ? result : (result?.data ?? []);

        if (!res.ok || !Array.isArray(tracks) || tracks.length === 0) {
            const emptyHtml = `
                <div class="history-list">
                    <div class="history-item">
                        <div class="history-title">Belum ada lagu yang pernah diputar</div>
                        <div class="history-meta">Played history masih kosong</div>
                    </div>
                </div>
            `;
            if (el.innerHTML !== emptyHtml) {
                el.innerHTML = emptyHtml;
            }
            return;
        }

        el.innerHTML = `
            <div class="history-list">
                ${tracks.map(track => `
                    <div class="history-item">
                        <div class="history-title">${escapeHtml(removeExtension(track.fileName))}</div>
                        <div class="history-meta">Played at: ${escapeHtml(formatDateTime(track.playedAt))}</div>
                    </div>
                `).join("")}
            </div>
        `;
    } catch (err) {
        const el = document.getElementById("status");
        if (el) {
            el.innerHTML = "<span class='muted'>Cannot load played history</span>";
        }
        setError(`Error loading played history: ${err.message}`);
    }
}

async function playNext() {
    try {
        const res = await fetch(`${BASE_URL}/api/queue/play-next`, {
            method: "POST"
        });

        const data = await safeJson(res);

        if (!res.ok) {
            throw new Error(data?.message || "Failed to play next");
        }

        await sleep(300);
        await refreshAll();
    } catch (err) {
        setError(`Error playing next: ${err.message}`);
    }
}

async function skip() {
    try {
        const res = await fetch(`${BASE_URL}/api/queue/skip`, {
            method: "POST"
        });

        const data = await safeJson(res);

        if (!res.ok) {
            throw new Error(data?.message || "Failed to skip");
        }

        await sleep(300);
        await refreshAll();
    } catch (err) {
        setError(`Error skipping: ${err.message}`);
    }
}

async function removeItem(id) {
    try {
        const res = await fetch(`${BASE_URL}/api/queue/${id}`, {
            method: "DELETE"
        });

        const data = await safeJson(res);

        if (!res.ok) {
            throw new Error(data?.message || "Failed to remove queue item");
        }

        await refreshAll();
    } catch (err) {
        setError(`Error removing item: ${err.message}`);
    }
}

async function refreshAll() {
    setError("");
    await loadStatus();
    await loadCurrent();
    await loadQueue();
    await loadTracks();
    await loadPlayedHistory();
}

function removeExtension(fileName) {
    return fileName.replace(/\.[^/.]+$/, "");
}

function formatDateTime(dateTime) {
    if (!dateTime) return "-";

    const date = new Date(dateTime);
    return date.toLocaleString("id-ID", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function escapeHtml(str) {
    if (!str) return "";
    return str
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

window.refreshAll = refreshAll;
window.playNext = playNext;
window.skip = skip;
window.removeItem = removeItem;
window.addToQueue = addToQueue;
window.pauseTrack = pauseTrack;
window.resumeTrack = resumeTrack;
window.togglePlayPause = togglePlayPause;
window.loadWABotStatus = loadWABotStatus;
window.toggleSidebar = toggleSidebar;
window.showPanel = showPanel;

document.addEventListener("DOMContentLoaded", async () => {
    showPanel("dashboard");

    await loadStatus();
    await loadCurrent();
    await loadQueue();
    await loadTracks();
    await loadPlayedHistory();
    await loadWABotStatus();

    setupProgressInteraction();
    startLiveTimer();
    setupBottomVolumeInteraction();

    if (playerRefreshTimer) clearInterval(playerRefreshTimer);
    if (listRefreshTimer) clearInterval(listRefreshTimer);
    if (refreshTimer) clearInterval(refreshTimer);

    playerRefreshTimer = setInterval(loadStatus, 1000);
    listRefreshTimer = setInterval(async () => {
        await loadCurrent();
        await loadQueue();
        await loadTracks();
        await loadPlayedHistory();
    }, 5000);

    refreshTimer = setInterval(loadWABotStatus, 5000);
});