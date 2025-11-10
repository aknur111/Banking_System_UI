const API_BASE = '/api';

export function getToken() {
    return localStorage.getItem('jwtToken');
}

export function setToken(token) {
    localStorage.setItem('jwtToken', token);
}

export function clearToken() {
    localStorage.removeItem('jwtToken');
}

async function request(method, path, body) {
    const headers = {
        'Content-Type': 'application/json'
    };
    const token = getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const res = await fetch(API_BASE + path, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(text || (`HTTP error ${res.status}`));
    }

    if (res.status === 204) return null;
    return res.json();
}

export const httpClient = {
    get: (path) => request('GET', path),
    post: (path, body) => request('POST', path, body),
    put: (path, body) => request('PUT', path, body),
    del: (path) => request('DELETE', path)
};
