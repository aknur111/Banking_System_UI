// /assets/js/pages/indexPage.js
import { authApi } from '../api/authApi.js';
import { loginSuccess } from '../core/auth.js';
import { showToast } from '../core/ui.js';

const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');

loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const username = document.getElementById('loginUsername').value.trim();
  const password = document.getElementById('loginPassword').value;

  try {
    const data = await authApi.login(username, password);
    if (!data || !data.token) throw new Error('No token in response');
    await loginSuccess(data.token);
  } catch (err) {
    showToast('Sign-in failed', err.message || 'Unknown error', true);
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
    showToast('Account created', 'Now sign in with your credentials');
  } catch (err) {
    showToast('Registration failed', err.message || 'Unknown error', true);
  }
});
