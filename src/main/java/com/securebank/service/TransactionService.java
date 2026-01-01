package com.securebank.service;

import com.securebank.exception.AccountNotFoundException;
import com.securebank.exception.InsufficientFundsException;
import com.securebank.exception.TransactionException;
import com.securebank.model.Account;
import com.securebank.model.Transaction;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
public class TransactionService {

    private static final Logger logger = Logger.getLogger(TransactionService.class.getName());

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private ExecutorService executorService;
    
    // Thread pool size - configurable for different deployment environments
    @Value("${transaction.thread-pool-size:10}")
    private int threadPoolSize;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        logger.info("Transaction Service initialized with " + threadPoolSize + " threads");
    }

    @PreDestroy
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public Future<Transaction> processTransactionAsync(Long fromAccountId, Long toAccountId, 
                                                       BigDecimal amount, String description) {
        return executorService.submit(() -> {
            return processTransaction(fromAccountId, toAccountId, amount, description);
        });
    }

    public Transaction processTransaction(Long fromAccountId, Long toAccountId, 
                                          BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException("Transaction amount must be positive");
        }

        // Fetch accounts
        Account fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new AccountNotFoundException("Source account not found: " + fromAccountId));
        
        Account toAccount = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new AccountNotFoundException("Destination account not found: " + toAccountId));

        // Create transaction record
        Transaction transaction = new Transaction(fromAccountId, toAccountId, amount, "TRANSFER", description);
        transaction.setStatus("PENDING");
        transaction = transactionRepository.save(transaction);

        // Lock accounts in a consistent order to prevent deadlocks
        Account firstLock = fromAccountId < toAccountId ? fromAccount : toAccount;
        Account secondLock = fromAccountId < toAccountId ? toAccount : fromAccount;

        try {
            firstLock.acquireLock();
            try {
                secondLock.acquireLock();
                try {
                    // Check balance
                    if (fromAccount.getBalance().compareTo(amount) < 0) {
                        transaction.setStatus("FAILED");
                        transactionRepository.updateStatus(transaction.getId(), "FAILED");
                        throw new InsufficientFundsException(
                            "Insufficient funds in account " + fromAccountId + 
                            ". Available: " + fromAccount.getBalance() + ", Required: " + amount
                        );
                    }

                    // Perform transfer
                    BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
                    BigDecimal newToBalance = toAccount.getBalance().add(amount);

                    accountRepository.updateBalance(fromAccountId, newFromBalance);
                    accountRepository.updateBalance(toAccountId, newToBalance);

                    // Update account objects
                    fromAccount.setBalance(newFromBalance);
                    toAccount.setBalance(newToBalance);

                    // Update transaction status
                    transaction.setStatus("COMPLETED");
                    transactionRepository.updateStatus(transaction.getId(), "COMPLETED");

                    logger.info("Transaction completed: " + transaction.getId() + 
                               " from " + fromAccountId + " to " + toAccountId + 
                               " amount: " + amount);

                    return transaction;

                } finally {
                    secondLock.releaseLock();
                }
            } finally {
                firstLock.releaseLock();
            }
        } catch (InsufficientFundsException | AccountNotFoundException e) {
            throw e;
        } catch (Exception e) {
            transaction.setStatus("FAILED");
            transactionRepository.updateStatus(transaction.getId(), "FAILED");
            throw new TransactionException("Transaction failed: " + e.getMessage(), e);
        }
    }

    public Transaction deposit(Long accountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException("Deposit amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));

        Transaction transaction = new Transaction(null, accountId, amount, "DEPOSIT", description);
        transaction.setStatus("PENDING");
        transaction = transactionRepository.save(transaction);

        try {
            account.acquireLock();
            try {
                BigDecimal newBalance = account.getBalance().add(amount);
                accountRepository.updateBalance(accountId, newBalance);
                account.setBalance(newBalance);

                transaction.setStatus("COMPLETED");
                transactionRepository.updateStatus(transaction.getId(), "COMPLETED");

                logger.info("Deposit completed: " + transaction.getId() + 
                           " to account " + accountId + " amount: " + amount);

                return transaction;
            } finally {
                account.releaseLock();
            }
        } catch (Exception e) {
            transaction.setStatus("FAILED");
            transactionRepository.updateStatus(transaction.getId(), "FAILED");
            throw new TransactionException("Deposit failed: " + e.getMessage(), e);
        }
    }

    public Transaction withdraw(Long accountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException("Withdrawal amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));

        Transaction transaction = new Transaction(accountId, null, amount, "WITHDRAWAL", description);
        transaction.setStatus("PENDING");
        transaction = transactionRepository.save(transaction);

        try {
            account.acquireLock();
            try {
                if (account.getBalance().compareTo(amount) < 0) {
                    transaction.setStatus("FAILED");
                    transactionRepository.updateStatus(transaction.getId(), "FAILED");
                    throw new InsufficientFundsException(
                        "Insufficient funds. Available: " + account.getBalance() + 
                        ", Required: " + amount
                    );
                }

                BigDecimal newBalance = account.getBalance().subtract(amount);
                accountRepository.updateBalance(accountId, newBalance);
                account.setBalance(newBalance);

                transaction.setStatus("COMPLETED");
                transactionRepository.updateStatus(transaction.getId(), "COMPLETED");

                logger.info("Withdrawal completed: " + transaction.getId() + 
                           " from account " + accountId + " amount: " + amount);

                return transaction;
            } finally {
                account.releaseLock();
            }
        } catch (InsufficientFundsException e) {
            throw e;
        } catch (Exception e) {
            transaction.setStatus("FAILED");
            transactionRepository.updateStatus(transaction.getId(), "FAILED");
            throw new TransactionException("Withdrawal failed: " + e.getMessage(), e);
        }
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
}
