function showPanel(panel) {
    const panels = [
        "dashboardPanel",
        "searchPanel",
        "lyricsPanel",
        "settingsPanel"
    ];

    panels.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.style.display = "none";
    });

    const active = document.getElementById(panel + "Panel");
    if (active) active.style.display = "block";
}

function toggleSidebar(forceExpand = false) {
    const sidebar = document.getElementById("sidebar");
    const appShell = document.getElementById("appShell");
    const logo = document.getElementById("brandLogo");
    const toggle = document.getElementById("sidebarToggle");

    if (forceExpand) {
        sidebar.classList.remove("minimized");
        appShell.classList.remove("minimized");
    } else {
        sidebar.classList.toggle("minimized");
        appShell.classList.toggle("minimized");
    }

    if (sidebar.classList.contains("minimized")) {
        logo.innerHTML = "☰";
        toggle.style.display = "none";
        logo.style.cursor = "pointer";
        logo.onclick = () => toggleSidebar(true);
    } else {
        logo.innerHTML = "♫";
        toggle.style.display = "block";
        logo.style.cursor = "default";
        logo.onclick = null;
    }
}