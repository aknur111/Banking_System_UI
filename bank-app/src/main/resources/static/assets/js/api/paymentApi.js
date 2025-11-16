// /assets/js/api/paymentApi.js
import { httpClient } from '../core/httpClient.js';

export const paymentApi = {
  my: () => httpClient.get('/payments/my'),
  create: (payload) => httpClient.post('/payments', payload),
};
