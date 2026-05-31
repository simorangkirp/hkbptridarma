let currentSong = {};

async function openLyrics(artist, title, cover) {
    currentSong = { artist, title, cover };

    navigate('/lyrics');

    document.getElementById("lyricsTitle").innerText = title;
    document.getElementById("lyricsArtist").innerText = artist;
    document.getElementById("lyricsCover").src = cover;
    document.getElementById("lyricsEditor").value = "Loading lyrics...";

    try {
        const data = await apiGetLyrics(artist, title);

        document.getElementById("lyricsEditor").value =
            data.lyrics || "Lyrics not found. You can paste lyrics manually.";
    } catch (err) {
        document.getElementById("lyricsEditor").value =
            "Failed to load lyrics.";
    }
}

async function saveSong() {
    const lyrics = document.getElementById("lyricsEditor").value;

    const payload = {
        title: currentSong.title,
        artist: currentSong.artist,
        coverImage: currentSong.cover,
        lyrics: lyrics
    };

    await apiSaveSong(payload);

    alert("Song saved!");
    navigate('/library');
}