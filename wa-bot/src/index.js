require("dotenv").config();

const qrcodeTerminal = require("qrcode-terminal");
const QRCode = require("qrcode");
const axios = require("axios");
const { Client, LocalAuth } = require("whatsapp-web.js");
const { handleMessage } = require("./handlers/messageHandler");

const BACKEND = process.env.BACKEND_BASE_URL;

const client = new Client({
    authStrategy: new LocalAuth(),
    puppeteer: {
        headless: true,
        args: ["--no-sandbox", "--disable-setuid-sandbox"]
    }
});

// === QR CODE EVENT ===
client.on("qr", async (qr) => {
    console.log("Scan QR below to login:");
    qrcodeTerminal.generate(qr, { small: true });

    try {
        // Convert QR → base64 image
        const qrBase64 = await QRCode.toDataURL(qr);

        // Send QR to backend
        await axios.post(`${BACKEND}/api/system/wa-qr`, {
            qr: qrBase64
        });

        console.log("QR sent to backend");
    } catch (err) {
        console.error("Failed to send QR to backend:", err.message);
    }
});

// === READY EVENT ===
client.on("ready", async () => {
    console.log("WhatsApp bot is ready.");

    try {
        await axios.post(`${BACKEND}/api/system/wa-ready`);
        console.log("Backend notified: WA ready");
    } catch (err) {
        console.error("Failed to notify backend WA ready:", err.message);
    }
});

// === AUTH EVENTS ===
client.on("authenticated", () => {
    console.log("WhatsApp authenticated.");
});

client.on("auth_failure", (msg) => {
    console.error("Auth failure:", msg);
});

// === DISCONNECTED EVENT ===
client.on("disconnected", async (reason) => {
    console.log("WhatsApp disconnected:", reason);

    try {
        await axios.post(`${BACKEND}/api/system/wa-disconnected`);
    } catch (err) {
        console.error("Failed to notify backend WA disconnected");
    }

    // reconnect
    client.initialize();
});

// === MESSAGE EVENT ===
client.on("message", async (message) => {
    await handleMessage(message);
});

// === START ===
client.initialize();