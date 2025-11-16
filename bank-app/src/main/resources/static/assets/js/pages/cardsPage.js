import {requireAuth, logout} from '../core/auth.js';
import {bindLogout, showToast} from '../core/ui.js';
import {cardApi} from '../api/cardApi.js';

requireAuth();
bindLogout('logoutBtn', logout);

const tbody = document.querySelector('#cardsTable tbody');
const refreshBtn = document.querySelector('#cardsRefreshBtn');
const freezeBtn = document.querySelector('#cardFreezeBtn');
const createDebitBtn = document.querySelector('#createDebitCardBtn');
const createCreditBtn = document.querySelector('#createCreditCardBtn');
const accountSelect = document.querySelector('#cardAccountSelect');

let selectedCardId = null;
let cardsCache = [];

async function loadCards() {
    try {
        const cards = await cardApi.myCards();
        cardsCache = cards || [];
        tbody.innerHTML = '';
        if (!cardsCache.length) {
            tbody.insertAdjacentHTML('beforeend', `
                <tr>
                    <td colspan="6" style="opacity:.7;padding:14px">No cards yet</td>
                </tr>
            `);
            setSelected(null);
            return;
        }
        cardsCache.forEach(c => {
            tbody.insertAdjacentHTML('beforeend', `
                <tr data-id="${c.id}">
                    <td>${c.maskedCardNumber}</td>
                    <td>${c.accountId}</td>
                    <td>${c.holderName || ''}</td>
                    <td>${c.status}</td>
                    <td>${c.expiryMonth}/${c.expiryYear}</td>
                    <td>${c.type}</td>
                </tr>
            `);
        });
        bindRowClicks();
        setSelected(cardsCache[0]?.id ?? null);
    } catch (e) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="color:#f87171;padding:14px">Failed to load cards</td>
            </tr>
        `;
        showToast('Error', 'Failed to load cards', 'error');
    }
}

function bindRowClicks() {
    tbody.querySelectorAll('tr[data-id]').forEach(tr => {
        tr.addEventListener('click', () => {
            const id = Number(tr.dataset.id);
            setSelected(id);
        });
    });
}

function setSelected(id) {
    selectedCardId = id;
    tbody.querySelectorAll('tr[data-id]').forEach(tr => {
        tr.classList.toggle('row-selected', Number(tr.dataset.id) === id);
    });
    updateDetails();
}

function updateDetails() {
    const panel = document.querySelector('#cardDetailsPanel');
    if (!selectedCardId) {
        panel.classList.add('hidden');
        return;
    }
    const card = cardsCache.find(c => c.id === selectedCardId);
    if (!card) {
        panel.classList.add('hidden');
        return;
    }
    panel.classList.remove('hidden');
    document.querySelector('#dCardNumber').textContent = card.maskedCardNumber;
    document.querySelector('#dAccount').textContent = card.accountId;
    document.querySelector('#dHolder').textContent = card.holderName || '';
    document.querySelector('#dStatus').textContent = card.status;
    document.querySelector('#dExpiry').textContent = `${card.expiryMonth}/${card.expiryYear}`;
    document.querySelector('#dType').textContent = card.type;
    freezeBtn.textContent = card.status === 'FROZEN' ? 'Unfreeze card' : 'Freeze card';
}

async function freezeToggle() {
    if (!selectedCardId) return;
    const card = cardsCache.find(c => c.id === selectedCardId);
    if (!card) return;
    try {
        let updated;
        if (card.status === 'FROZEN') {
            updated = await cardApi.unfreeze(selectedCardId);
            showToast('Success', 'Card unfrozen', 'success');
        } else {
            updated = await cardApi.freeze(selectedCardId);
            showToast('Success', 'Card frozen', 'success');
        }
        const idx = cardsCache.findIndex(c => c.id === selectedCardId);
        if (idx >= 0) cardsCache[idx] = updated;
        redrawRows();
        setSelected(selectedCardId);
    } catch (e) {
        showToast('Error', 'Failed to change card status', 'error');
    }
}

function redrawRows() {
    tbody.innerHTML = '';
    cardsCache.forEach(c => {
        tbody.insertAdjacentHTML('beforeend', `
            <tr data-id="${c.id}">
                <td>${c.maskedCardNumber}</td>
                <td>${c.accountId}</td>
                <td>${c.holderName || ''}</td>
                <td>${c.status}</td>
                <td>${c.expiryMonth}/${c.expiryYear}</td>
                <td>${c.type}</td>
            </tr>
        `);
    });
    bindRowClicks();
}

async function createCard(kind) {
    const accountId = Number(accountSelect.value);
    if (!accountId) {
        showToast('Error', 'Select account first', 'error');
        return;
    }
    try {
        const created = kind === 'DEBIT'
            ? await cardApi.createDebit(accountId)
            : await cardApi.createCredit(accountId);
        cardsCache.push(created);
        redrawRows();
        setSelected(created.id);
        showToast('Success', 'Card created', 'success');
    } catch (e) {
        showToast('Error', 'Failed to create card', 'error');
    }
}

refreshBtn?.addEventListener('click', loadCards);
freezeBtn?.addEventListener('click', freezeToggle);
createDebitBtn?.addEventListener('click', () => createCard('DEBIT'));
createCreditBtn?.addEventListener('click', () => createCard('CREDIT'));

loadCards();
