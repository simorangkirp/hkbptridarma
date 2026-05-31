const events = {};

export function on(event, callback) {
    if (!events[event]) {
        events[event] = [];
    }
    events[event].push(callback);
}

export function emit(event, data) {
    if (events[event]) {
        events[event].forEach(cb => cb(data));
    }
}