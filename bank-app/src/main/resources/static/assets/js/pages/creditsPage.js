import {requireAuth, logout} from '../core/auth.js';
import {bindLogout, showToast} from '../core/ui.js';
import {creditApi} from '../api/creditApi.js';

if (!requireAuth()) {
    window.location.href = '/index.html';
}

bindLogout('logoutBtn', logout);

const tbody = document.querySelector('#creditsTable tbody');
const refreshBtn = document.getElementById('refreshBtn');

async function loadCredits() {
    tbody.innerHTML = '';
    try {
        const credits = await creditApi.myCredits();

        if (!credits || credits.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" style="opacity:.7">No credits yet</td></tr>`;
            return;
        }

        credits.forEach((c, index) => {
            const amount = `${c.principalAmount} ${c.currency}`;
            const apr = `${c.interestRateAnnual}%`;
            const statusClass = c.status === 'ACTIVE'
                ? 'badge badge--success'
                : 'badge';

            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td>${index + 1}</td>
                    <td>${c.creditType}</td>
                    <td>${amount}</td>
                    <td>${apr}</td>
                    <td><span class="${statusClass}">${c.status}</span></td>
                </tr>
            `);
        });
    } catch (e) {
        console.error('Failed to load credits', e);
        tbody.innerHTML = `<tr><td colspan="5" style="color:#fb7185">Failed to load credits</td></tr>`;
        showToast('Error', 'Failed to load credits');
    }
}

refreshBtn?.addEventListener('click', loadCredits);
loadCredits();
