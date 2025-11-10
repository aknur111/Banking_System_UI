import {requireAuth, logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';
import {authApi} from '../api/authApi.js';

requireAuth();
bindLogout('logoutBtn', logout);

(async function init() {
    try {
        const me = await authApi.me();
        document.getElementById('profileFullName').value = me.fullName || '';
        document.getElementById('profilePhone').value = me.phone || '';
        document.getElementById('profileUsername').value = me.username || '';
    } catch (e) {
        console.error(e);
    }
})();
