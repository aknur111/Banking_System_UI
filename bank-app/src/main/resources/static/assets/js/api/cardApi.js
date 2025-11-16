import {httpClient} from '../core/httpClient.js';

export const cardApi = {
    myCards: () => httpClient.get('/cards/my'),
    getCard: id => httpClient.get(`/cards/${id}`),
    freeze: id => httpClient.post(`/cards/${id}/freeze`),
    unfreeze: id => httpClient.post(`/cards/${id}/unfreeze`),
    createDebit: accountId => httpClient.post('/cards/debit', {accountId}),
    createCredit: accountId => httpClient.post('/cards/credit', {accountId})
};
