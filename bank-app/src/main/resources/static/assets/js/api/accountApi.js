import {httpClient} from '../core/httpClient.js';

export const accountApi = {
    myAccounts: () => httpClient.get('/accounts/my'),
    getAccount: (id) => httpClient.get(`/accounts/${id}`),
    create: (currency) => httpClient.post('/accounts', {currency})
};
