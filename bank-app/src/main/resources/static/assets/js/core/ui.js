let toastTimeout;

export function showToast(title, text, isError = false) {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    container.className = 'toast ' + (isError ? 'toast--error' : '');
    container.innerHTML = `
        <div class="toast__title">${title}</div>
        <div class="toast__text">${text || ''}</div>
    `;
    container.classList.remove('hidden');

    clearTimeout(toastTimeout);
    toastTimeout = setTimeout(() => {
        container.classList.add('hidden');
    }, 3500);
}

export function bindLogout(buttonId, logoutFn) {
    const btn = document.getElementById(buttonId);
    if (btn) btn.addEventListener('click', logoutFn);
}
