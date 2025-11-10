import {httpClient} from '../core/httpClient.js';

export const paymentApi = {
    payFromAccountNow: (accountId, body) =>
        httpClient.post(`/payments/account/${accountId}/pay`, body),
    payFromCardNow: (cardId, body) =>
        httpClient.post(`/payments/card/${cardId}/pay`, body),
    myPayments: (customerId) =>
        httpClient.get(`/payments/customer/${customerId}`)
};
