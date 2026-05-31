let allSearchResults = [];

async function searchSong(query) {
    if (!query || query.length < 2) {
        document.getElementById("searchResultsGrid").innerHTML = "";
        document.getElementById("seeAllBtn").style.display = "none";
        return;
    }

    const data = await apiSearchSong(query);
    allSearchResults = data.data || [];

    renderGrid(allSearchResults.slice(0, 8));

    document.getElementById("seeAllBtn").style.display =
        allSearchResults.length > 8 ? "block" : "none";
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
                    <i data-lucide="play" class="icon-btn"></i>
                    <i data-lucide="file-text" class="icon-btn"
                       onclick="openLyrics('${artist}', '${title}', '${cover}')"></i>
                </div>
            </div>
        `;
    });

    document.getElementById("searchResultsGrid").innerHTML = html;

    lucide.createIcons();
    setupMarquee();
}

function showAllResults() {
    renderGrid(allSearchResults);
}

function setupMarquee() {
    document.querySelectorAll(".song-title").forEach(el => {
        const inner = el.querySelector(".title-inner");

        if (inner.scrollWidth > el.clientWidth) {
            inner.innerHTML = inner.innerText + "   " + inner.innerText;
            inner.classList.add("marquee");
        }
    });
}

function formatDuration(sec) {
    const minutes = Math.floor(sec / 60);
    const seconds = sec % 60;
    return `${minutes}:${seconds.toString().padStart(2, "0")}`;
}