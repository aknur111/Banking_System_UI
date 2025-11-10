import {httpClient, setToken, clearToken, getToken} from './httpClient.js';

export async function login(username, password) {
    const data = await httpClient.post('/auth/login', { username, password });
    if (data.token) {
        setToken(data.token);
    }
    return data;
}

export async function register(payload) {
    return httpClient.post('/auth/register', payload);
}

export function logout() {
    clearToken();
    window.location.href = '/index.html';
}

export function requireAuth() {
    if (!getToken()) {
        window.location.href = '/index.html';
    }
}
