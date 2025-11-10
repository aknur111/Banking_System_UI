import {httpClient} from '../core/httpClient.js';

export const bonusApi = {
    getBalance: () => httpClient.get('/bonuses/account'),
    history: () => httpClient.get('/bonuses/history'),
    rules: () => httpClient.get('/bonuses/rules')
};
