package com.securebank.repository;

import com.securebank.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Account> accountRowMapper = new RowMapper<Account>() {
        @Override
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            Account account = new Account();
            account.setId(rs.getLong("id"));
            account.setAccountNumber(rs.getString("account_number"));
            account.setAccountHolderName(rs.getString("account_holder_name"));
            account.setEmail(rs.getString("email"));
            account.setBalance(rs.getBigDecimal("balance"));
            account.setAccountType(rs.getString("account_type"));
            account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            account.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return account;
        }
    };

    public List<Account> findAll() {
        String sql = "SELECT * FROM accounts ORDER BY id";
        return jdbcTemplate.query(sql, accountRowMapper);
    }

    public Optional<Account> findById(Long id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        List<Account> accounts = jdbcTemplate.query(sql, accountRowMapper, id);
        return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts.get(0));
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        List<Account> accounts = jdbcTemplate.query(sql, accountRowMapper, accountNumber);
        return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts.get(0));
    }

    public Account save(Account account) {
        if (account.getId() == null) {
            String sql = "INSERT INTO accounts (account_number, account_holder_name, email, balance, account_type, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
            jdbcTemplate.update(sql, 
                account.getAccountNumber(),
                account.getAccountHolderName(),
                account.getEmail(),
                account.getBalance(),
                account.getAccountType()
            );
            return findByAccountNumber(account.getAccountNumber()).orElse(account);
        } else {
            String sql = "UPDATE accounts SET account_holder_name = ?, email = ?, balance = ?, account_type = ?, updated_at = NOW() WHERE id = ?";
            jdbcTemplate.update(sql,
                account.getAccountHolderName(),
                account.getEmail(),
                account.getBalance(),
                account.getAccountType(),
                account.getId()
            );
            return account;
        }
    }

    public void updateBalance(Long accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ?, updated_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, newBalance, accountId);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM accounts";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}
