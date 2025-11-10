import {requireAuth, logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';
import {connectNotifications} from '../core/websocketClient.js';
import {accountApi} from '../api/accountApi.js';
import {transactionApi} from '../api/transactionApi.js';

requireAuth();
bindLogout('logoutBtn', logout);
connectNotifications();

const summaryRoot = document.getElementById('summaryCards');
const txTableBody = document.querySelector('#lastTransactionsTable tbody');

(async function init() {
    try {
        const accounts = await accountApi.myAccounts();
        renderSummary(accounts);
    } catch (e) {
        console.error(e);
    }

    try {
        const tx = await transactionApi.myTransactions();
        renderTransactions(tx.slice(0, 10));
    } catch (e) {
        console.error(e);
    }
})();

function renderSummary(accounts = []) {
    summaryRoot.innerHTML = '';

    const total = accounts.reduce((sum, a) => sum + Number(a.balance || 0), 0);

    summaryRoot.insertAdjacentHTML('beforeend', `
        <div class="card">
            <div class="card__title">Общий баланс</div>
            <div class="card__main">${total.toLocaleString('ru-RU')} ₸</div>
        </div>
    `);

    accounts.slice(0, 2).forEach(a => {
        summaryRoot.insertAdjacentHTML('beforeend', `
            <div class="card card--soft">
                <div class="card__title">Счёт #${a.id}</div>
                <div class="card__main">${Number(a.balance).toLocaleString('ru-RU')} ${a.currency}</div>
            </div>
        `);
    });
}

function renderTransactions(list = []) {
    txTableBody.innerHTML = '';
    list.forEach(t => {
        txTableBody.insertAdjacentHTML('beforeend', `
            <tr>
                <td>${t.createdAt || ''}</td>
                <td>${t.accountId || ''}</td>
                <td>${t.description || ''}</td>
                <td>${t.direction === 'OUT' ? '-' : '+'}${t.amount} ${t.currency}</td>
                <td><span class="badge badge--muted">${t.status}</span></td>
            </tr>
        `);
    });
}
