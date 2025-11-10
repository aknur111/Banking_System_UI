import {getToken} from './httpClient.js';
import {showToast} from './ui.js';

// очень простой клиент поверх WebSocket + JWT в query
export function connectNotifications() {
    const token = getToken();
    if (!token) return;

    const wsUrl = `ws://${window.location.host}/ws?token=${encodeURIComponent(token)}`;
    const socket = new WebSocket(wsUrl);

    socket.onmessage = (event) => {
        try {
            const notif = JSON.parse(event.data);
            showToast('Уведомление', notif.message || 'Новое уведомление');
        } catch {
            showToast('Уведомление', event.data);
        }
    };

    socket.onclose = () => {
        // можно попробовать переподключиться позже, если нужно
    };

    return socket;
}
