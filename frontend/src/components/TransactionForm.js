import React, { useState } from 'react';
import { transactionService } from '../services/api';
import './TransactionForm.css';

const TransactionForm = ({ accounts, onTransactionComplete }) => {
  const [activeTab, setActiveTab] = useState('transfer');
  const [formData, setFormData] = useState({
    fromAccountId: '',
    toAccountId: '',
    accountId: '',
    amount: '',
    description: ''
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const resetForm = () => {
    setFormData({
      fromAccountId: '',
      toAccountId: '',
      accountId: '',
      amount: '',
      description: ''
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage(null);

    try {
      let response;
      const amount = parseFloat(formData.amount);

      if (activeTab === 'transfer') {
        response = await transactionService.transfer(
          parseInt(formData.fromAccountId),
          parseInt(formData.toAccountId),
          amount,
          formData.description || 'Transfer'
        );
      } else if (activeTab === 'deposit') {
        response = await transactionService.deposit(
          parseInt(formData.accountId),
          amount,
          formData.description || 'Deposit'
        );
      } else if (activeTab === 'withdraw') {
        response = await transactionService.withdraw(
          parseInt(formData.accountId),
          amount,
          formData.description || 'Withdrawal'
        );
      }

      setMessage({ type: 'success', text: `Transaction completed successfully! ID: ${response.data.id}` });
      resetForm();
      if (onTransactionComplete) onTransactionComplete();
    } catch (err) {
      setMessage({ 
        type: 'error', 
        text: err.response?.data?.message || 'Transaction failed: ' + err.message 
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="transaction-form">
      <h2>New Transaction</h2>
      
      <div className="tab-buttons">
        <button
          className={activeTab === 'transfer' ? 'active' : ''}
          onClick={() => setActiveTab('transfer')}
        >
          Transfer
        </button>
        <button
          className={activeTab === 'deposit' ? 'active' : ''}
          onClick={() => setActiveTab('deposit')}
        >
          Deposit
        </button>
        <button
          className={activeTab === 'withdraw' ? 'active' : ''}
          onClick={() => setActiveTab('withdraw')}
        >
          Withdraw
        </button>
      </div>

      <form onSubmit={handleSubmit}>
        {activeTab === 'transfer' && (
          <>
            <div className="form-group">
              <label>From Account</label>
              <select
                name="fromAccountId"
                value={formData.fromAccountId}
                onChange={handleChange}
                required
              >
                <option value="">Select account</option>
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>
                    {acc.accountNumber} - {acc.accountHolderName} (${acc.balance.toFixed(2)})
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>To Account</label>
              <select
                name="toAccountId"
                value={formData.toAccountId}
                onChange={handleChange}
                required
              >
                <option value="">Select account</option>
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>
                    {acc.accountNumber} - {acc.accountHolderName}
                  </option>
                ))}
              </select>
            </div>
          </>
        )}

        {(activeTab === 'deposit' || activeTab === 'withdraw') && (
          <div className="form-group">
            <label>Account</label>
            <select
              name="accountId"
              value={formData.accountId}
              onChange={handleChange}
              required
            >
              <option value="">Select account</option>
              {accounts.map(acc => (
                <option key={acc.id} value={acc.id}>
                  {acc.accountNumber} - {acc.accountHolderName} (${acc.balance.toFixed(2)})
                </option>
              ))}
            </select>
          </div>
        )}

        <div className="form-group">
          <label>Amount</label>
          <input
            type="number"
            name="amount"
            value={formData.amount}
            onChange={handleChange}
            placeholder="0.00"
            step="0.01"
            min="0.01"
            required
          />
        </div>

        <div className="form-group">
          <label>Description (Optional)</label>
          <input
            type="text"
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Enter description"
          />
        </div>

        {message && (
          <div className={`message ${message.type}`}>
            {message.text}
          </div>
        )}

        <button type="submit" className="submit-button" disabled={loading}>
          {loading ? 'Processing...' : `Submit ${activeTab}`}
        </button>
      </form>
    </div>
  );
};

export default TransactionForm;
