import { httpClient } from '../core/httpClient.js';

export const accountApi = {
  myAccounts: () => httpClient.get('/accounts/my')
};
