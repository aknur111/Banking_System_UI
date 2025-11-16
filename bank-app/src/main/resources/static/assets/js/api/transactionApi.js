// /assets/js/api/transactionApi.js
import { httpClient } from '../core/httpClient.js';

export const transactionApi = {
  myTransactions: () => httpClient.get('/transactions/my'),
  accountTransactions: (accountId) =>
    httpClient.get(`/transactions/account/${accountId}`),
  create: (payload) => httpClient.post('/transactions', payload),
};
