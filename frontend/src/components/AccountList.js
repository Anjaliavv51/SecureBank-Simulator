import React, { useState, useEffect } from 'react';
import { accountService } from '../services/api';
import './AccountList.css';

const AccountList = ({ onSelectAccount }) => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadAccounts();
  }, []);

  const loadAccounts = async () => {
    try {
      setLoading(true);
      const response = await accountService.getAllAccounts();
      setAccounts(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to load accounts: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const filteredAccounts = accounts.filter(account =>
    account.accountNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
    account.accountHolderName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    account.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) return <div className="loading">Loading accounts...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="account-list">
      <div className="account-list-header">
        <h2>Accounts ({accounts.length})</h2>
        <input
          type="text"
          placeholder="Search accounts..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
      </div>
      <div className="account-grid">
        {filteredAccounts.map(account => (
          <div
            key={account.id}
            className="account-card"
            onClick={() => onSelectAccount && onSelectAccount(account)}
          >
            <div className="account-header">
              <span className="account-number">{account.accountNumber}</span>
              <span className={`account-type ${account.accountType.toLowerCase()}`}>
                {account.accountType}
              </span>
            </div>
            <h3>{account.accountHolderName}</h3>
            <p className="account-email">{account.email}</p>
            <div className="account-balance">
              <span className="balance-label">Balance:</span>
              <span className="balance-amount">${account.balance.toFixed(2)}</span>
            </div>
          </div>
        ))}
      </div>
      {filteredAccounts.length === 0 && (
        <div className="no-results">No accounts found</div>
      )}
    </div>
  );
};

export default AccountList;
