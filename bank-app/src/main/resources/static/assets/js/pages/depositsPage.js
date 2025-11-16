import {requireAuth, logout} from '../core/auth.js';
import {bindLogout, showToast} from '../core/ui.js';
import {depositApi} from '../api/depositApi.js';

if (!requireAuth()) {
  window.location.href = '/index.html';
}

bindLogout('logoutBtn', logout);

document.querySelectorAll('.nav-list a').forEach(a => {
  const href = a.getAttribute('href');
  if (href && location.pathname.endsWith(href)) {
    a.closest('.nav-item')?.classList.add('nav-item--active');
  }
});

const tbody = document.querySelector('#depositsTable tbody');
const refreshBtn = document.getElementById('refreshBtn');

refreshBtn.addEventListener('click', loadDeposits);

async function loadDeposits() {
  tbody.innerHTML = `<tr><td colspan="5" style="opacity:.7">Loading...</td></tr>`;
  try {
    const deposits = await depositApi.myDeposits();
    tbody.innerHTML = '';
    if (!deposits || deposits.length === 0) {
      tbody.innerHTML = `<tr><td colspan="5" style="opacity:.7">No deposits yet</td></tr>`;
      return;
    }
    deposits.forEach(d => {
      const principal = Number(d.principalAmount ?? 0).toLocaleString('en-US');
      tbody.insertAdjacentHTML('beforeend', `
        <tr>
          <td>${d.id}</td>
          <td>${d.accountId}</td>
          <td>${principal} ${d.currency}</td>
          <td>${d.monthlyInterest}%</td>
          <td><span class="badge ${d.status === 'ACTIVE' ? 'badge--success' : 'badge--muted'}">${d.status}</span></td>
        </tr>
      `);
    });
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="5" style="color:#fb7185">Failed to load deposits</td></tr>`;
    showToast('Error', 'Failed to load deposits');
  }
}

loadDeposits();
