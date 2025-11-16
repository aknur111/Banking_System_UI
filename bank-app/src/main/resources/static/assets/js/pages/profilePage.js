import {requireAuth, logout} from '../core/auth.js';
import {bindLogout, showToast} from '../core/ui.js';
import {profileApi} from '../api/profileApi.js';

if (!requireAuth()) {
    window.location.href = '/index.html';
}

bindLogout('logoutBtn', logout);

const form = document.getElementById('profileForm');
const fullNameInput = document.getElementById('profileFullName');
const phoneInput = document.getElementById('profilePhone');
const usernameInput = document.getElementById('profileUsername');

const clientIdEl = document.getElementById('profileClientId');
const roleEl = document.getElementById('profileRole');
const emailEl = document.getElementById('profileEmail');
const riskEl = document.getElementById('profileRisk');
const memberSinceEl = document.getElementById('profileMemberSince');

async function loadProfile() {
    try {
        const p = await profileApi.me();

        fullNameInput.value = p.fullName || '';
        phoneInput.value = p.phone || '';
        usernameInput.value = p.username || '';

        if (clientIdEl) clientIdEl.textContent = p.id ?? '—';
        if (roleEl) roleEl.textContent = p.role || '—';
        if (emailEl) emailEl.textContent = p.email || '—';
        if (riskEl) riskEl.textContent = p.riskProfile || '—';
        if (memberSinceEl) {
            memberSinceEl.textContent = p.memberSince
                ? new Date(p.memberSince).toLocaleDateString()
                : '—';
        }
    } catch (e) {
        console.error('Failed to load profile', e);
        showToast('Error', 'Failed to load profile');
    }
}

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        await profileApi.update({
            fullName: fullNameInput.value.trim(),
            phone: phoneInput.value.trim(),
        });
        showToast('Profile', 'Changes saved');
        await loadProfile();
    } catch (e) {
        console.error('Failed to save profile', e);
        showToast('Error', 'Failed to save profile');
    }
});

loadProfile();
