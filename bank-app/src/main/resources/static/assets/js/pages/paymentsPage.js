import {requireAuth, logout} from '../core/auth.js';
import {bindLogout, showToast} from '../core/ui.js';
import {paymentApi} from '../api/paymentApi.js';

requireAuth();
bindLogout('logoutBtn', logout);

const form = document.getElementById('paymentForm');
const paymentsBody = document.querySelector('#paymentsTable tbody');

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const sourceType = document.getElementById('paymentSourceType').value;
    const sourceId = Number(document.getElementById('paymentSourceId').value);
    const body = {
        amount: Number(document.getElementById('paymentAmount').value),
        currency: document.getElementById('paymentCurrency').value,
        category: document.getElementById('paymentCategory').value,
        providerName: document.getElementById('paymentProvider').value,
        detailsJson: document.getElementById('paymentDetails').value
    };

    try {
        let payment;
        if (sourceType === 'ACCOUNT') {
            payment = await paymentApi.payFromAccountNow(sourceId, body);
        } else {
            payment = await paymentApi.payFromCardNow(sourceId, body);
        }
        showToast('Платёж выполнен', `${payment.amount} ${payment.currency}`);
        loadHistory();
    } catch (err) {
        showToast('Ошибка платежа', err.message, true);
    }
});

// history – тут нужен customerId, ты можешь заменить на свой способ
async function loadHistory() {
    paymentsBody.innerHTML = '';
    // временно просто оставим пусто — или если есть customerId, подставь
}

loadHistory().catch(console.error);
