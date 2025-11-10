import {httpClient} from '../core/httpClient.js';

export const cardApi = {
    myCards: () => httpClient.get('/cards/my')
};
