const axios = require("axios");

const api = axios.create({
    baseURL: process.env.BACKEND_BASE_URL,
    timeout: 10000
});

async function getStatus() {
    const res = await api.get("/api/player/status");
    return res.data;
}

async function getTracks() {
    const res = await api.get("/api/player/tracks");
    return res.data;
}

async function getQueue() {
    const res = await api.get("/api/queue");
    return res.data;
}

async function playNext() {
    const res = await api.post("/api/queue/play-next");
    return res.data;
}

async function skip() {
    const res = await api.post("/api/queue/skip");
    return res.data;
}

async function pause() {
    const res = await api.post("/api/player/pause");
    return res.data;
}

async function resume() {
    const res = await api.post("/api/player/resume");
    return res.data;
}

async function addToQueue(fileName) {
    const res = await api.post("/api/queue/add", { fileName });
    return res.data;
}

async function removeQueueItem(id) {
    const res = await api.delete(`/api/queue/${id}`);
    return res.data;
}

async function registerTrack(fileName, filePath) {
    const res = await api.post("/api/tracks/register", {
        fileName: fileName,
        filePath: filePath
    });
    return res.data;
}

module.exports = {
    getStatus,
    getTracks,
    getQueue,
    playNext,
    skip,
    pause,
    resume,
    addToQueue,
    removeQueueItem, 
    registerTrack,
};