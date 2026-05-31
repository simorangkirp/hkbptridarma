function normalizeNumber(chatId = "") {
    return chatId.replace("@c.us", "").trim();
}

function getAllowedNumbers() {
    const raw = process.env.ALLOWED_NUMBERS || "";
    return raw
        .split(",")
        .map(item => item.trim())
        .filter(Boolean);
}

function isAllowedNumber(chatId) {
    const normalized = normalizeNumber(chatId);
    return getAllowedNumbers().includes(normalized);
}

module.exports = {
    normalizeNumber,
    getAllowedNumbers,
    isAllowedNumber
};