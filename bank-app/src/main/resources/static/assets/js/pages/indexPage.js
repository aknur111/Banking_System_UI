import {authApi} from '../api/authApi.js';
import {setToken} from '../core/httpClient.js';
import {showToast} from '../core/ui.js';

const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;

    try {
        const data = await authApi.login(username, password);
        setToken(data.token);
        showToast('Успешный вход', `Здравствуйте, ${username}`);
        window.location.href = '/dashboard.html';
    } catch (err) {
        showToast('Ошибка входа', err.message, true);
    }
});

registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        username: document.getElementById('regUsername').value.trim(),
        password: document.getElementById('regPassword').value,
        fullName: document.getElementById('regFullName').value.trim(),
        phone: document.getElementById('regPhone').value.trim()
    };

    try {
        await authApi.register(payload);
        showToast('Готово', 'Регистрация успешна, теперь войдите');
    } catch (err) {
        showToast('Ошибка регистрации', err.message, true);
    }
});
