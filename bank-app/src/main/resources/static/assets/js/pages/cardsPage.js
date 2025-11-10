import {requireAuth, logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';
import {cardApi} from '../api/cardApi.js';

requireAuth();
bindLogout('logoutBtn', logout);

const tbody = document.querySelector('#cardsTable tbody');

(async function init() {
    const cards = await cardApi.myCards().catch(() => []);
    tbody.innerHTML = '';
    cards.forEach(c => {
        tbody.insertAdjacentHTML('beforeend', `
            <tr>
                <td>**** **** **** ${String(c.cardNumber).slice(-4)}</td>
                <td>${c.accountId}</td>
                <td><span class="badge badge--${c.status === 'ACTIVE' ? 'success' : 'muted'}">${c.status}</span></td>
                <td>${c.expiryMonth}/${c.expiryYear}</td>
            </tr>
        `);
    });
})();
