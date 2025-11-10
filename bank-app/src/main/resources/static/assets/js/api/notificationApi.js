import {httpClient} from '../core/httpClient.js';

export const notificationApi = {
    myNotifications: () => httpClient.get('/notifications/my'),
    unread: () => httpClient.get('/notifications/my/unread'),
    markAllRead: () => httpClient.post('/notifications/my/mark-all-read', {})
};
