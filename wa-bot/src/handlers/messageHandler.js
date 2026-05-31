const path = require("path");
const backendService = require("../services/backendService");
const { saveBase64File } = require("../services/mediaService");
const { isAllowedNumber } = require("../utils/auth");
const { getSession, setMode, clearSession } = require("../utils/sessionStore");

function buildMenuText() {
    return `
*HORAS!*
Selamat datang di ruang perbincangan dengan asisten *SiButet* 🤍

Butet siap bantu urusan player gereja ya.
Silakan pilih apa yang bisa Butet bantu:

1. menu
2. status
3. tracks
4. queue
5. play next
6. skip
7. pause
8. resume
9. add
10. remove <queue id>

Tinggal ketik angka atau nama perintahnya ya ✨
    `.trim();
}

function buildAddPromptText() {
    return `
Siap, silakan upload file yang mau Butet bantu putarkan ya 🎶

Format file yang boleh:
- mp3
- mp4
- m4a
- wav

Setelah file dikirim, Butet akan simpan ke folder incoming dulu ya.
Kalau mau batal atau kembali ke menu, tinggal balas *menu* 🤍
    `.trim();
}

function formatStatus(data) {
    return `
*Status Player Saat Ini* 🎵

Status: ${data.status ?? "-"}
Track: ${data.currentTrack ?? "-"}
Posisi: ${Math.floor(data.currentTimeSeconds ?? 0)} detik
Durasi total: ${Math.floor(data.totalDurationSeconds ?? 0)} detik
Volume: ${Math.round((data.volume ?? 0) * 100)}%

Kalau masih ada yang mau dicek, tinggal bilang ke Butet ya ✨
    `.trim();
}

function formatTracks(tracks) {
    if (!Array.isArray(tracks) || tracks.length === 0) {
        return "Saat ini belum ada file di folder incoming ya 🤍";
    }

    return [
        "*Daftar Incoming Tracks* 🎶",
        "Ini file yang berhasil Butet temukan:",
        "",
        ...tracks.map((track, index) => `${index + 1}. ${track.fileName}`),
        "",
        "Kalau mau upload file baru, ketik *add* ya."
    ].join("\n");
}

function formatQueue(queue) {
    if (!Array.isArray(queue) || queue.length === 0) {
        return "Queue masih kosong ya. Belum ada lagu yang antre 🤍";
    }

    return [
        "*Daftar Queue Saat Ini* 🎼",
        "Ini lagu-lagu yang sedang antre:",
        "",
        ...queue.map((item, index) => `${index + 1}. ${item.fileName} | ${item.id}`),
        "",
        "Kalau ada yang mau dihapus, tinggal pakai perintah *remove <queue id>* ya."
    ].join("\n");
}

function resolveIncomingFileName(message, media) {
    const rawName =
        message?._data?.filename ||
        media?.filename ||
        null;

    if (rawName) {
        return rawName;
    }

    const mime = media?.mimetype || "";
    const extFromMime = mime.includes("/")
        ? `.${mime.split("/")[1].split(";")[0]}`
        : "";

    return `upload_${Date.now()}${extFromMime || ".bin"}`;
}

async function handleIncomingMedia(message) {
    const media = await message.downloadMedia();

    if (!media) {
        await message.reply("Failed to download attached file.");
        return;
    }

    const fileName = resolveIncomingFileName(message, media);

    const result = saveBase64File({
        fileName,
        base64Data: media.data
    });

    const savedName = path.basename(result.filePath);

    await message.reply(
        [
            `Yeay, file berhasil Butet terima 🤍`,
            `Nama file: ${savedName}`,
            `File sudah Butet simpan ke folder incoming.`,
            `File ini belum masuk ke queue ya, nanti operator multimedia bisa cek dulu dari dashboard ✨`
        ].join("\n")
    );
}

function normalizeCommandInput(rawText) {
    const text = (rawText || "").trim().toLowerCase();

    const commandMap = {
        "1": "menu",
        "2": "status",
        "3": "tracks",
        "4": "queue",
        "5": "play next",
        "6": "skip",
        "7": "pause",
        "8": "resume",
        "9": "add"
    };

    return commandMap[text] || text;
}

async function handleMessage(message) {
    const chatId = message.from;

    console.log("handleMessage called from:", chatId);

    if (!isAllowedNumber(chatId)) {
        console.log("Blocked number:", chatId);
        return;
    }

    const session = getSession(chatId);

    try {
        const rawText = (message.body || "").trim();
        const text = normalizeCommandInput(rawText);

        if (text === "menu") {
            clearSession(chatId);
            await message.reply(buildMenuText());
            return;
        }

        if (message.hasMedia) {
            if (session.mode !== "WAITING_UPLOAD") {
                await message.reply(
                    'Kalau mau upload file, ketik *add* dulu ya 🤍\nKalau mau lihat pilihan yang tersedia, balas *menu*.'
                );
            }

            await handleIncomingMedia(message);
            clearSession(chatId);
            return;
        }

        if (text === "add") {
            setMode(chatId, "WAITING_UPLOAD");
            await message.reply(buildAddPromptText());
            return;
        }

        if (text === "status") {
            const data = await backendService.getStatus();
            await message.reply(formatStatus(data));
            return;
        }

        if (text === "tracks") {
            const data = await backendService.getTracks();
            await message.reply(formatTracks(data));
            return;
        }

        if (text === "queue") {
            const data = await backendService.getQueue();
            await message.reply(formatQueue(data));
            return;
        }

        if (text === "play next") {
            await backendService.playNext();
            await message.reply("Siap, Butet bantu putarkan track berikutnya ya 🎶");
            return;
        }

        if (text === "skip") {
            await backendService.skip();
            await message.reply("Oke, track yang tadi sudah Butet lewati ya ⏭️");
            return;
        }

        if (text === "pause") {
            await backendService.pause();
            await message.reply("Siap, pemutaran sudah Butet pause dulu ya ⏸️");
            return;
        }

        if (text === "resume") {
            await backendService.resume();
            await message.reply("Oke, pemutaran Butet lanjutkan lagi ya ▶️");
            return;
        }

        if (text.startsWith("remove ")) {
            const id = rawText.slice(7).trim();

            if (!id) {
                await message.reply("Butet belum bisa baca id queue-nya nih.\nCoba pakai format: *remove <queue-id>* ya 🤍");
                return;
            }

            await backendService.removeQueueItem(id);
            await message.reply(`Siap, item queue dengan id *${id}* sudah Butet hapus ya 🗑️`);
            return;
        }

        await message.reply('Hmm, Butet belum paham maksud pesan itu 😅\nBalas *menu* ya biar Butet kasih pilihan yang tersedia.');
    } catch (error) {
        console.error("handleMessage error:", error);

        const msg =
            error?.response?.data?.message ||
            error?.message ||
            "Unknown error";

        await message.reply(`Aduh, Butet nemu kendala nih 🥲\nDetailnya: ${msg}`);
    }
}

module.exports = {
    handleMessage
};