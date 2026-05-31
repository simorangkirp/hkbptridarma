export const state = {
    player: {
        status: "STOPPED",
        currentTrack: null,
        currentTime: 0,
        duration: 0,
        volume: 1
    },
    queue: [],
    tracks: [],
    searchResults: [],
    currentLyrics: null
};

const listeners = [];

export function subscribe(fn) {
    listeners.push(fn);
}

export function setState(partial) {
    Object.assign(state, partial);
    listeners.forEach(fn => fn(state));
}