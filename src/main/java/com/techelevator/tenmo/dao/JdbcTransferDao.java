package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean save(Transfer transfer) {
        String sql =
                "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount, message) " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING transfer_id;";
                 try {
                     int id = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getTransfer_type_id(), transfer.getTransfer_status_id(), transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount(), transfer.getMessage());
                     transfer.setTransfer_id(id);
                 } catch (DataAccessException e) {
                     return false;
                 }
        return true;
    }

    public List<Transfer> listTransfers(int id) {
        List<Transfer> list = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE transfer_status_id = 2 AND (account_from = ? OR account_to = ?);";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id, id);
        while (rowSet.next()){
            Transfer transfer = mapRowToTransfer(rowSet);
            list.add(transfer);
        }
        return list;
    }

    @Override
    public List<Transfer> pendingTransfers(int id) {
        List<Transfer> list = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE transfer_status_id = 1 AND account_from = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        while (rowSet.next()){
            Transfer transfer = mapRowToTransfer(rowSet);
            list.add(transfer);
        }
        return list;
    }

    public void approveOrReject(int id, int action){
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?;";
        try {
            jdbcTemplate.update(sql, action, id);
        } catch (DataAccessException e) {
           throw new IllegalStateException("no");
        }
    }

    @Override
    public Transfer getTransfer(int id) {
        Transfer transfer = new Transfer();
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()) {
            transfer = mapRowToTransfer(rowSet);
        }
        return transfer;
    }


    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransfer_id(rs.getInt("transfer_id"));
        transfer.setTransfer_type_id(rs.getInt("transfer_type_id"));
        transfer.setTransfer_status_id(rs.getInt("transfer_status_id"));
        transfer.setAccount_to(rs.getInt("account_to"));
        transfer.setAccount_from(rs.getInt("account_from"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        transfer.setMessage(rs.getString("message"));
        return transfer;
    }


}
