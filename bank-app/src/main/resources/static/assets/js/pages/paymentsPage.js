// /assets/js/pages/paymentsPage.js
import { requireAuth, logout } from '../core/auth.js';
import { bindLogout, showToast } from '../core/ui.js';
import { paymentApi } from '../api/paymentApi.js';

requireAuth();
bindLogout('logoutBtn', logout);

const form = document.getElementById('paymentForm');  // убедись, что id есть в HTML
const tableBody = document.querySelector('#paymentsTable tbody');

async function loadHistory() {
  try {
    const list = await paymentApi.my();
    renderHistory(Array.isArray(list) ? list : []);
  } catch (e) {
    console.error(e);
    showToast('Payments', 'Failed to load payments history', true);
  }
}

function renderHistory(items) {
  if (!tableBody) return;
  tableBody.innerHTML = '';
  if (!items.length) {
    tableBody.innerHTML = `<tr><td colspan="4">No payments yet</td></tr>`;
    return;
  }

  items.forEach(p => {
    const dt = p.createdAt ? new Date(p.createdAt) : null;
    const dateStr = dt && !Number.isNaN(dt.getTime())
      ? dt.toLocaleDateString('en-US')
      : '';

    const amount = Number(p.amount || 0);
    const sign = amount < 0 ? '-' : '';
    const cls = amount < 0 ? 'amount-neg' : 'amount-pos';

    tableBody.insertAdjacentHTML(
      'beforeend',
      `
      <tr>
        <td>${dateStr}</td>
        <td>${p.category || ''}</td>
        <td class="${cls}">${sign}${Math.abs(amount).toLocaleString('en-US')} ${p.currency || ''}</td>
        <td>${p.status || ''}</td>
      </tr>
      `
    );
  });
}

if (form) {
  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const payload = {
      sourceType: document.getElementById('sourceType').value || 'ACCOUNT',
      sourceId: Number(document.getElementById('sourceId').value || '1'),
      amount: Number(document.getElementById('amount').value || '0'),
      currency: document.getElementById('currency').value || 'KZT',
      category: document.getElementById('category').value,
      provider: document.getElementById('provider').value,
      comment: document.getElementById('comment').value,
    };

    try {
      await paymentApi.create(payload);
      showToast('Payment success', 'Payment was created');
      form.reset();
      await loadHistory();
    } catch (err) {
      console.error(err);
      showToast('Payment failed', err.message || 'Error while creating payment', true);
    }
  });
}

loadHistory().catch(() => {});
