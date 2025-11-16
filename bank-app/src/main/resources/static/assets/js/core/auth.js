// /assets/js/core/auth.js
import { getToken, setToken, clearToken } from './httpClient.js';

export function requireAuth() {
  const token = getToken();
  if (!token && window.location.pathname !== '/index.html') {
    window.location.replace('/index.html');
    return false;
  }
  return true;
}

export async function loginSuccess(token) {
  setToken(token, true);
  // важно: replace, чтобы кнопка back не возвращала на index и не запускала скрипты логина заново
  window.location.replace('/dashboard.html');
}

export function logout() {
  clearToken();
  window.location.replace('/index.html');
}
