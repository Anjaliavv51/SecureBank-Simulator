import React, { useState, useEffect } from 'react';
import AccountList from './components/AccountList';
import TransactionForm from './components/TransactionForm';
import TransactionHistory from './components/TransactionHistory';
import { accountService } from './services/api';
import './App.css';

function App() {
  const [accounts, setAccounts] = useState([]);
  const [activeView, setActiveView] = useState('accounts');
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    loadAccounts();
  }, [refreshKey]);

  const loadAccounts = async () => {
    try {
      const response = await accountService.getAllAccounts();
      setAccounts(response.data);
    } catch (error) {
      console.error('Error loading accounts:', error);
    }
  };

  const handleTransactionComplete = () => {
    setRefreshKey(prev => prev + 1);
  };

  const handleSelectAccount = (account) => {
    setSelectedAccount(account);
    setActiveView('account-detail');
  };

  return (
    <div className="App">
      <header className="app-header">
        <div className="header-content">
          <h1>🏦 SecureBank Simulator</h1>
          <p className="tagline">Multi-threaded Banking System</p>
        </div>
      </header>

      <nav className="app-nav">
        <button
          className={activeView === 'accounts' ? 'active' : ''}
          onClick={() => setActiveView('accounts')}
        >
          All Accounts
        </button>
        <button
          className={activeView === 'transaction' ? 'active' : ''}
          onClick={() => setActiveView('transaction')}
        >
          New Transaction
        </button>
        <button
          className={activeView === 'history' ? 'active' : ''}
          onClick={() => setActiveView('history')}
        >
          Transaction History
        </button>
      </nav>

      <main className="app-main">
        {activeView === 'accounts' && (
          <AccountList 
            key={refreshKey}
            onSelectAccount={handleSelectAccount}
          />
        )}
        
        {activeView === 'transaction' && (
          <TransactionForm
            accounts={accounts}
            onTransactionComplete={handleTransactionComplete}
          />
        )}
        
        {activeView === 'history' && (
          <TransactionHistory key={refreshKey} />
        )}
        
        {activeView === 'account-detail' && selectedAccount && (
          <div className="account-detail">
            <button 
              className="back-button"
              onClick={() => setActiveView('accounts')}
            >
              ← Back to Accounts
            </button>
            <div className="account-info">
              <h2>{selectedAccount.accountHolderName}</h2>
              <p className="account-number">{selectedAccount.accountNumber}</p>
              <p className="balance">Balance: ${selectedAccount.balance.toFixed(2)}</p>
            </div>
            <TransactionHistory 
              accountId={selectedAccount.id} 
              key={`${selectedAccount.id}-${refreshKey}`}
            />
          </div>
        )}
      </main>

      <footer className="app-footer">
        <p>&copy; 2024 SecureBank Simulator - Multi-threaded Banking System with Spring Boot & React</p>
      </footer>
    </div>
  );
}

export default App;
