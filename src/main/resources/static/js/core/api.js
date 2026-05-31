const BASE_URL = "http://localhost:8087";

async function apiSearchSong(query) {
    const res = await fetch(`${BASE_URL}/api/search?query=${encodeURIComponent(query)}`);
    return await res.json();
}

async function apiGetLyrics(artist, title) {
    const res = await fetch(`${BASE_URL}/api/lyrics?artist=${encodeURIComponent(artist)}&title=${encodeURIComponent(title)}`);
    return await res.json();
}

async function apiSaveSong(data) {
    const res = await fetch(`${BASE_URL}/api/songs`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });

    return await res.json();
}