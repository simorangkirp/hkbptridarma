const fs = require("fs");
const path = require("path");

const ALLOWED_EXTENSIONS = [".mp3", ".mp4", ".m4a", ".wav"];

function sanitizeFileName(fileName) {
    return fileName
        .replace(/[<>:"/\\|?*\x00-\x1F]/g, "_")
        .replace(/\s+/g, " ")
        .trim();
}

function ensureIncomingDir() {
    const incomingDir = path.resolve(__dirname, "../../../incoming");
    if (!fs.existsSync(incomingDir)) {
        fs.mkdirSync(incomingDir, { recursive: true });
    }
    return incomingDir;
}

function getSafeFileName(originalName) {
    const sanitized = sanitizeFileName(originalName || "file");
    const ext = path.extname(sanitized).toLowerCase();
    const baseName = path.basename(sanitized, ext);

    if (!ALLOWED_EXTENSIONS.includes(ext)) {
        throw new Error(`File type not allowed: ${ext || "unknown"}`);
    }

    const incomingDir = ensureIncomingDir();
    let finalName = sanitized;
    let counter = 1;

    while (fs.existsSync(path.join(incomingDir, finalName))) {
        finalName = `${baseName} (${counter})${ext}`;
        counter++;
    }

    return finalName;
}

function saveBase64File({ fileName, base64Data }) {
    const incomingDir = ensureIncomingDir();
    const safeFileName = getSafeFileName(fileName);
    const filePath = path.join(incomingDir, safeFileName);

    fs.writeFileSync(filePath, Buffer.from(base64Data, "base64"));

    return {
        fileName: safeFileName,
        filePath
    };
}

module.exports = {
    saveBase64File
};