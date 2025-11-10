import {httpClient} from '../core/httpClient.js';

export const transactionApi = {
    myTransactions: () => httpClient.get('/transactions/my')
};
