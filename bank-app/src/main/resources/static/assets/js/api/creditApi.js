import {httpClient} from '../core/httpClient.js';

export const creditApi = {
    myCredits: () => httpClient.get('/credits/my')
};
