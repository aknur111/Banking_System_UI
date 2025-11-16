import {httpClient} from '../core/httpClient.js';

export const profileApi = {
    me: () => httpClient.get('/profile/me'),
    update: (payload) => httpClient.put('/profile/me', payload),
};
