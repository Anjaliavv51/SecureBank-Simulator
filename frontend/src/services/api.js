import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const accountService = {
  getAllAccounts: () => api.get('/accounts'),
  getAccountById: (id) => api.get(`/accounts/${id}`),
  getAccountByNumber: (accountNumber) => api.get(`/accounts/number/${accountNumber}`),
  createAccount: (account) => api.post('/accounts', account),
  updateAccount: (id, account) => api.put(`/accounts/${id}`, account),
  deleteAccount: (id) => api.delete(`/accounts/${id}`),
  getAccountCount: () => api.get('/accounts/count'),
};

export const transactionService = {
  getAllTransactions: () => api.get('/transactions'),
  getTransactionById: (id) => api.get(`/transactions/${id}`),
  getTransactionsByAccountId: (accountId) => api.get(`/transactions/account/${accountId}`),
  transfer: (fromAccountId, toAccountId, amount, description) => 
    api.post('/transactions/transfer', { fromAccountId, toAccountId, amount, description }),
  deposit: (accountId, amount, description) => 
    api.post('/transactions/deposit', { accountId, amount, description }),
  withdraw: (accountId, amount, description) => 
    api.post('/transactions/withdraw', { accountId, amount, description }),
};

export default api;
