import {httpClient} from '../core/httpClient.js';

export const authApi = {
    login: (username, password) =>
        httpClient.post('/auth/login', {username, password}),
    register: (payload) =>
        httpClient.post('/auth/register', payload),
    me: () => httpClient.get('/users/me') // если сделаешь такой эндпоинт
};
