const sessions = new Map();

function getSession(chatId) {
    if (!sessions.has(chatId)) {
        sessions.set(chatId, {
            mode: "IDLE"
        });
    }

    return sessions.get(chatId);
}

function setMode(chatId, mode) {
    const session = getSession(chatId);
    session.mode = mode;
    sessions.set(chatId, session);
}

function clearSession(chatId) {
    sessions.set(chatId, {
        mode: "IDLE"
    });
}

module.exports = {
    getSession,
    setMode,
    clearSession
};