package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getAccount(int id) {
        Account account = null;
        String sql = "SELECT * FROM account " +
                "WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()) {
            account = mapRowToAccount(rowSet);
        }
        return account;
    }

    @Override
    public User getUser(int id) {
        User user = null;
        String sql = "SELECT t.user_id, t.username FROM tenmo_user t " +
                "JOIN account a ON a.user_id = t.user_id " +
                "WHERE a.account_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()){
            user = mapRowToUser(rowSet);
        }
        return user;
    }

    @Override
    public List<User> listUsers(int id) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT user_id, username FROM tenmo_user WHERE user_id != ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        while (rowSet.next()) {
            User account = mapRowToUser(rowSet);
            userList.add(account);
        }
        return userList;
    }

    public void updateBalances(int toId, int fromId, BigDecimal amountTo, BigDecimal amountFrom) {
        String sql = "UPDATE account SET balance = balance + ? WHERE account_id = ?; " +
                "UPDATE account SET balance = balance - ? WHERE account_id = ?";
        try {
            jdbcTemplate.update(sql, amountTo, toId, amountFrom, fromId);
        } catch (DataAccessException e) {
            throw new IllegalStateException("no");
        }
    }


    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setActivated(true);
        user.setUsername(rs.getString("username"));
        return user;
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccount_id(rs.getInt("account_id"));
        account.setUser_id(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
