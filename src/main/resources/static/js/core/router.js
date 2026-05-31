const routes = {
    "/dashboard": "/pages/dashboard.html",
    "/search": "/pages/search.html",
    "/lyrics": "/pages/lyrics.html",
    "/settings": "/pages/settings.html"
};

async function navigate(path) {
    history.pushState({}, "", path);
    router();
}

async function router() {
    const path = window.location.pathname;
    const page = routes[path] || "/pages/dashboard.html";

    const res = await fetch(page);
    const html = await res.text();
    document.getElementById("appContent").innerHTML = html;
}

window.onpopstate = router;