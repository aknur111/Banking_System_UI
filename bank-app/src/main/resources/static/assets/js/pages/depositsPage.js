import {requireAuth, logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';
import {depositApi} from '../api/depositApi.js';

requireAuth();
bindLogout('logoutBtn', logout);

const tbody = document.querySelector('#depositsTable tbody');

(async function init() {
    const deposits = await depositApi.myDeposits().catch(() => []);
    tbody.innerHTML = '';
    deposits.forEach(d => {
        tbody.insertAdjacentHTML('beforeend', `
            <tr>
                <td>${d.id}</td>
                <td>${d.accountId}</td>
                <td>${d.principalAmount} ${d.currency}</td>
                <td>${d.monthlyInterest}%</td>
                <td>${d.status}</td>
            </tr>
        `);
    });
})();
