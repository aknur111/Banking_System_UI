import {requireAuth, logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';
import {bonusApi} from '../api/bonusApi.js';

requireAuth();
bindLogout('logoutBtn', logout);

const balanceEl = document.getElementById('bonusBalance');
const rulesEl = document.getElementById('cashbackRules');
const historyBody = document.querySelector('#bonusTxTable tbody');

(async function init() {
    try {
        const balance = await bonusApi.getBalance();
        balanceEl.textContent = `${Number(balance.balance || 0).toLocaleString('ru-RU')} ₸`;
    } catch {}

    try {
        const rules = await bonusApi.rules();
        rulesEl.innerHTML = rules.map(r =>
            `<div style="font-size:0.85rem;margin-bottom:0.25rem;">
               <b>${r.name}</b> – ${r.percent}% ${r.category ? ' на ' + r.category : ''}
             </div>`
        ).join('');
    } catch {}

    try {
        const history = await bonusApi.history();
        historyBody.innerHTML = '';
        history.forEach(h => {
            historyBody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>${h.createdAt || ''}</td>
                    <td>${h.description || ''}</td>
                    <td>${h.amount}</td>
                </tr>
            `);
        });
    } catch {}
})();
