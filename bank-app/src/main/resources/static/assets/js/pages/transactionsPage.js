import {requireAuth, logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';
import {transactionApi} from '../api/transactionApi.js';

requireAuth();
bindLogout('logoutBtn', logout);

const tbody = document.querySelector('#transactionsTable tbody');

(async function init() {
    const tx = await transactionApi.myTransactions().catch(() => []);
    tbody.innerHTML = '';
    tx.forEach(t => {
        tbody.insertAdjacentHTML('beforeend', `
            <tr>
                <td>${t.createdAt || ''}</td>
                <td>${t.accountId || ''}</td>
                <td>${t.description || ''}</td>
                <td>${t.direction === 'OUT' ? '-' : '+'}${t.amount} ${t.currency}</td>
                <td>${t.status}</td>
            </tr>
        `);
    });
})();
