async function loadComponent(id, file) {
    const res = await fetch(file);
    const html = await res.text();
    document.getElementById(id).innerHTML = html;
}

async function loadLayout() {
    await loadComponent("sidebar", "/components/sidebar.html");
    await loadComponent("bottomPlayer", "/components/bottom-player.html");
}