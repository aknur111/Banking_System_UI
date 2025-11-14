// /assets/js/core/httpClient.js
const API_BASE = '/api';

export function getToken() {
  return localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
}

export function setToken(token, persist = true) {
  if (persist) localStorage.setItem('jwtToken', token);
  else sessionStorage.setItem('jwtToken', token);
}

export function clearToken() {
  localStorage.removeItem('jwtToken');
  sessionStorage.removeItem('jwtToken');
}

async function request(method, path, body) {
  const headers = { 'Content-Type': 'application/json' };
  const token = getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(API_BASE + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });

  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(text || `HTTP error ${res.status}`);
  }
  if (res.status === 204) return null;
  return res.json();
}

export const httpClient = {
  get: (p) => request('GET', p),
  post: (p, b) => request('POST', p, b),
  put: (p, b) => request('PUT', p, b),
  del: (p) => request('DELETE', p)
};
