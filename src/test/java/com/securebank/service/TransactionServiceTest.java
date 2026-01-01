package com.securebank.service;

import com.securebank.exception.AccountNotFoundException;
import com.securebank.exception.InsufficientFundsException;
import com.securebank.model.Account;
import com.securebank.model.Transaction;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        transactionService.init();
        
        fromAccount = new Account(1L, "ACC001", "John Doe", "john@example.com", 
                                  new BigDecimal("1000.00"), "SAVINGS");
        toAccount = new Account(2L, "ACC002", "Jane Doe", "jane@example.com", 
                                new BigDecimal("500.00"), "CHECKING");
    }

    @Test
    void testTransferSuccess() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        // Act
        Transaction result = transactionService.processTransaction(1L, 2L, 
                                                                   new BigDecimal("100.00"), 
                                                                   "Test transfer");

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(accountRepository, times(2)).updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void testTransferInsufficientFunds() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> {
            transactionService.processTransaction(1L, 2L, 
                                                  new BigDecimal("2000.00"), 
                                                  "Test transfer");
        });
    }

    @Test
    void testTransferAccountNotFound() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            transactionService.processTransaction(1L, 2L, 
                                                  new BigDecimal("100.00"), 
                                                  "Test transfer");
        });
    }

    @Test
    void testDepositSuccess() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        // Act
        Transaction result = transactionService.deposit(1L, 
                                                       new BigDecimal("500.00"), 
                                                       "Test deposit");

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(accountRepository, times(1)).updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void testWithdrawSuccess() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        // Act
        Transaction result = transactionService.withdraw(1L, 
                                                        new BigDecimal("200.00"), 
                                                        "Test withdrawal");

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(accountRepository, times(1)).updateBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void testWithdrawInsufficientFunds() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> {
            transactionService.withdraw(1L, 
                                       new BigDecimal("2000.00"), 
                                       "Test withdrawal");
        });
    }
}
