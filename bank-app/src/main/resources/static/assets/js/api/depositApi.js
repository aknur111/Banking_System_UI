// /assets/js/api/depositApi.js
import {httpClient} from '../core/httpClient.js';

export const depositApi = {
    // ВАЖНО: без "api" здесь
    myDeposits: () => httpClient.get('/deposits/my')
};


