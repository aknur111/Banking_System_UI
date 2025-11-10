import {requireAuth, logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';
import {creditApi} from '../api/creditApi.js';

requireAuth();
bindLogout('logoutBtn', logout);

const tbody = document.querySelector('#creditsTable tbody');

(async function init() {
    const credits = await creditApi.myCredits().catch(() => []);
    tbody.innerHTML = '';
    credits.forEach(c => {
        tbody.insertAdjacentHTML('beforeend', `
            <tr>
                <td>${c.id}</td>
                <td>${c.creditType}</td>
                <td>${c.principalAmount} ${c.currency}</td>
                <td>${c.interestRateAnnual}%</td>
                <td>${c.status}</td>
            </tr>
        `);
    });
})();
