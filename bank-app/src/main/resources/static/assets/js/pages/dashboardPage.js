// /assets/js/pages/dashboardPage.js
import { getToken } from '../core/httpClient.js';
import { requireAuth, logout } from '../core/auth.js';
import { bindLogout } from '../core/ui.js';
import { connectNotifications } from '../core/websocketClient.js';
import { accountApi } from '../api/accountApi.js';
import { transactionApi } from '../api/transactionApi.js';

if (!getToken()) {
  // если токен не нужен, можно закомментить, но пусть будет —
  // сейчас всё равно backend всё пускает
  requireAuth();
}

bindLogout('logoutBtn', logout);
try {
  connectNotifications();
} catch { /* ignore */ }

const summaryRoot = document.getElementById('summaryCards');
const txTableBody = document.querySelector('#lastTransactionsTable tbody');

// навешиваем обработчики на кнопки
const btnOwn = document.getElementById('btnTransferOwn');
const btnClient = document.getElementById('btnTransferClient');
const btnBills = document.getElementById('btnPayBills');

if (btnOwn) btnOwn.addEventListener('click', () => {
  window.location.href = '/payments.html#own';
});
if (btnClient) btnClient.addEventListener('click', () => {
  window.location.href = '/payments.html#client';
});
if (btnBills) btnBills.addEventListener('click', () => {
  window.location.href = '/payments.html#bills';
});

(async function init() {
  const accounts = await safe(() => accountApi.myAccounts(), []);
  renderSummary(accounts);

  const tx = await safe(() => transactionApi.myTransactions(), []);
  renderTransactions(Array.isArray(tx) ? tx.slice(0, 10) : []);
})();

function renderSummary(accounts = []) {
  if (!summaryRoot) return;
  summaryRoot.innerHTML = '';

  const total = accounts.reduce((s, a) => s + Number(a?.balance || 0), 0);

  summaryRoot.insertAdjacentHTML(
    'beforeend',
    `
    <div class="card">
      <div class="label">Total balance</div>
      <div class="value">${fmt(total)} ₸</div>
      <div class="muted" style="font-size:.85rem">across all accounts</div>
    </div>
    `
  );

  const top = accounts.slice(0, 2);
  const fill = top.length ? top : [{}, {}];

  fill.forEach((a, i) => {
    const label = a && a.id ? `Account #${a.id}` : i === 0 ? 'Checking account' : 'Savings account';
    const value = a && a.balance != null ? `${fmt(a.balance)} ${a.currency ?? ''}` : '—';
    const meta = a && a.type ? a.type : 'no data';
    summaryRoot.insertAdjacentHTML(
      'beforeend',
      `
      <div class="card">
        <div class="label">${label}</div>
        <div class="value">${value}</div>
        <div class="muted" style="font-size:.85rem">${meta}</div>
      </div>
      `
    );
  });

  summaryRoot.insertAdjacentHTML(
    'beforeend',
    `
    <div class="card card--wide">
      <div class="label">Credit load</div>
      <div class="value muted">0 ₸</div>
      <div class="muted" style="font-size:.85rem">no active loans</div>
    </div>
    `
  );
}

function renderTransactions(list = []) {
  if (!txTableBody) return;
  txTableBody.innerHTML = '';
  if (!list.length) {
    txTableBody.insertAdjacentHTML(
      'beforeend',
      `<tr><td colspan="5" class="muted">No transactions yet</td></tr>`
    );
    return;
  }

  list.forEach(t => {
    const out = String(t.direction).toUpperCase() === 'OUT';
    const sign = out ? '-' : '+';
    const amount = `${sign}${fmt(t.amount)} ${t.currency ?? ''}`;
    const cls = out ? 'amount-neg' : 'amount-pos'; // совпадает с css
    const status = String(t.status || '');
    const done = ['completed', 'done', 'success'].includes(status.toLowerCase());
    const badge = done
      ? `<span class="badge badge--success">Completed</span>`
      : `<span class="badge badge--muted">${status || '—'}</span>`;

    txTableBody.insertAdjacentHTML(
      'beforeend',
      `
      <tr>
        <td>${dateFmt(t.createdAt)}</td>
        <td>${t.description ?? ''}</td>
        <td>${t.accountId ?? ''}</td>
        <td class="${cls}">${amount}</td>
        <td>${badge}</td>
      </tr>
      `
    );
  });
}

function fmt(n) {
  const v = Number(n || 0);
  return Number.isFinite(v) ? v.toLocaleString('en-US') : '0';
}

function dateFmt(d) {
  if (!d) return '';
  const dt = typeof d === 'string' ? new Date(d) : d;
  return Number.isNaN(dt.getTime()) ? String(d) : dt.toLocaleDateString('en-US');
}

async function safe(fn, fallback) {
  try {
    return await fn();
  } catch (e) {
    console.error('dashboard request failed', e);
    return fallback;
  }
}
