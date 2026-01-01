import React, { useState, useEffect } from 'react';
import { transactionService } from '../services/api';
import './TransactionHistory.css';

const TransactionHistory = ({ accountId }) => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadTransactions();
  }, [accountId]);

  const loadTransactions = async () => {
    try {
      setLoading(true);
      const response = accountId 
        ? await transactionService.getTransactionsByAccountId(accountId)
        : await transactionService.getAllTransactions();
      setTransactions(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to load transactions: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  const getTransactionTypeClass = (type) => {
    return type.toLowerCase();
  };

  const getStatusClass = (status) => {
    return status.toLowerCase();
  };

  if (loading) return <div className="loading">Loading transactions...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="transaction-history">
      <h2>Recent Transactions</h2>
      {transactions.length === 0 ? (
        <div className="no-transactions">No transactions found</div>
      ) : (
        <div className="transaction-list">
          {transactions.map(transaction => (
            <div key={transaction.id} className="transaction-item">
              <div className="transaction-main">
                <div className="transaction-info">
                  <span className={`transaction-type ${getTransactionTypeClass(transaction.transactionType)}`}>
                    {transaction.transactionType}
                  </span>
                  <span className="transaction-id">#{transaction.id}</span>
                </div>
                <div className="transaction-details">
                  <p className="transaction-description">
                    {transaction.description || 'No description'}
                  </p>
                  <p className="transaction-accounts">
                    {transaction.fromAccountId && `From: ${transaction.fromAccountId}`}
                    {transaction.fromAccountId && transaction.toAccountId && ' → '}
                    {transaction.toAccountId && `To: ${transaction.toAccountId}`}
                  </p>
                </div>
              </div>
              <div className="transaction-right">
                <div className={`transaction-amount ${transaction.transactionType.toLowerCase()}`}>
                  {transaction.transactionType === 'WITHDRAWAL' ? '-' : '+'}
                  ${transaction.amount.toFixed(2)}
                </div>
                <div className={`transaction-status ${getStatusClass(transaction.status)}`}>
                  {transaction.status}
                </div>
                <div className="transaction-date">
                  {formatDate(transaction.createdAt)}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default TransactionHistory;
