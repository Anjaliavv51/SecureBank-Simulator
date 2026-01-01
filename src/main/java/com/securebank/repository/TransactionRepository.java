package com.securebank.repository;

import com.securebank.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Transaction> transactionRowMapper = new RowMapper<Transaction>() {
        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getLong("id"));
            transaction.setFromAccountId(rs.getLong("from_account_id"));
            transaction.setToAccountId(rs.getLong("to_account_id"));
            transaction.setAmount(rs.getBigDecimal("amount"));
            transaction.setTransactionType(rs.getString("transaction_type"));
            transaction.setStatus(rs.getString("status"));
            transaction.setDescription(rs.getString("description"));
            transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return transaction;
        }
    };

    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper);
    }

    public Optional<Transaction> findById(Long id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        List<Transaction> transactions = jdbcTemplate.query(sql, transactionRowMapper, id);
        return transactions.isEmpty() ? Optional.empty() : Optional.of(transactions.get(0));
    }

    public List<Transaction> findByAccountId(Long accountId) {
        String sql = "SELECT * FROM transactions WHERE from_account_id = ? OR to_account_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, accountId, accountId);
    }

    public Transaction save(Transaction transaction) {
        String sql = "INSERT INTO transactions (from_account_id, to_account_id, amount, transaction_type, status, description, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql,
            transaction.getFromAccountId(),
            transaction.getToAccountId(),
            transaction.getAmount(),
            transaction.getTransactionType(),
            transaction.getStatus(),
            transaction.getDescription()
        );
        
        // Get the last inserted transaction
        String findSql = "SELECT * FROM transactions ORDER BY id DESC LIMIT 1";
        List<Transaction> transactions = jdbcTemplate.query(findSql, transactionRowMapper);
        return transactions.isEmpty() ? transaction : transactions.get(0);
    }

    public void updateStatus(Long id, String status) {
        String sql = "UPDATE transactions SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, id);
    }
}
