// /assets/js/pages/dashboardPage.js
import { getToken } from '../core/httpClient.js';
import { requireAuth, logout } from '../core/auth.js';
import { bindLogout } from '../core/ui.js';
import { connectNotifications } from '../core/websocketClient.js';
import { accountApi } from '../api/accountApi.js';
import { transactionApi } from '../api/transactionApi.js';

if (!getToken()) {
  window.location.href = '/index.html';
} else {
  requireAuth();
}

bindLogout('logoutBtn', logout);
try { connectNotifications(); } catch {}

const summaryRoot = document.getElementById('summaryCards');
const txTableBody = document.querySelector('#lastTransactionsTable tbody');

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
      <div class="card__label">Total balance</div>
      <div class="card__value">${fmt(total)} ₸</div>
      <div class="card__meta">across all accounts</div>
    </div>
    `
  );

  const top = accounts.slice(0, 2);
  const fill = top.length ? top : [{}, {}];

  fill.forEach((a, i) => {
    const label = a && a.id ? \`Account #\${a.id}\` : i === 0 ? 'Checking account' : 'Savings account';
    const value = a && a.balance != null ? \`\${fmt(a.balance)} \${a.currency ?? ''}\` : '—';
    const meta = a && a.type ? a.type : 'no data';
    summaryRoot.insertAdjacentHTML(
      'beforeend',
      `
      <div class="card">
        <div class="card__label">${label}</div>
        <div class="card__value">${value}</div>
        <div class="card__meta">${meta}</div>
      </div>
      `
    );
  });

  summaryRoot.insertAdjacentHTML(
    'beforeend',
    `
    <div class="card card--wide">
      <div class="card__label">Credit load</div>
      <div class="card__value card__value--muted">0 ₸</div>
      <div class="card__meta">no active loans</div>
    </div>
    `
  );
}

function renderTransactions(list = []) {
  if (!txTableBody) return;
  txTableBody.innerHTML = '';
  if (!list.length) return;

  list.forEach(t => {
    const out = String(t.direction).toUpperCase() === 'OUT';
    const sign = out ? '-' : '+';
    const amount = `${sign}${fmt(t.amount)} ${t.currency ?? ''}`;
    const cls = out ? 'amount--neg' : 'amount--pos';
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
        <td>${t.accountId ?? ''}</td>
        <td>${t.description ?? ''}</td>
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
  } catch {
    return fallback;
  }
}
