import {httpClient} from '../core/httpClient.js';

export const authApi = {
    login: (email, password) =>
        httpClient.post('/auth/login', {email, password}),
    register: (payload) =>
        httpClient.post('/auth/register', payload),
    me: () => httpClient.get('/users/me') // если сделаешь такой эндпоинт
};
