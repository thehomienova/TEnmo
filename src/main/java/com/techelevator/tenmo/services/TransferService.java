package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    private AuthenticatedUser currentUser;

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }

    public TransferService(String url) {
        this.baseUrl = url;
    }

    public User getUserByAccountId(int id){
        User user = null;
        try {
            ResponseEntity<User> response =
                    restTemplate.exchange(baseUrl + "user/" + id, HttpMethod.GET, makeAuthEntity(), User.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if (user == null) {
            throw new NullPointerException("No User found");
        } else {
            return user;
        }
    }

    public Account getAccountByUserId(int id){
        Account account = null;
        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseUrl + id, HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public boolean sendBucks(int id, BigDecimal amount, String message) {
        Account toAccount = getAccountByUserId(id);
        Account fromAccount = getAccountByUserId(currentUser.getUser().getId());


        boolean sufficientFunds = false;
        int result = fromAccount.getBalance().compareTo(amount);
        if (result < 1) {
            return sufficientFunds;
        } else {
            Transfer transfer = new Transfer();
            transfer.setAmount(amount);
            transfer.setAccount_from(fromAccount.getAccount_id());
            transfer.setAccount_to(toAccount.getAccount_id());
            transfer.setTransfer_type_id(2);
            transfer.setTransfer_status_id(2);
            transfer.setMessage(message);

            try {
                HttpEntity<Transfer> entity = makeTransferEntity(transfer);
                ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "/send", HttpMethod.POST, entity, Transfer.class);
                transfer = response.getBody();
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
            return sufficientFunds = true;
        }

    }

    public void requestBucks(int id, BigDecimal amount, String message) {
        Account fromAccount = getAccountByUserId(id);
        Account toAccount = getAccountByUserId(currentUser.getUser().getId());


            Transfer transfer = new Transfer();
            transfer.setAmount(amount);
            transfer.setAccount_from(fromAccount.getAccount_id());
            transfer.setAccount_to(toAccount.getAccount_id());
            transfer.setTransfer_type_id(1);
            transfer.setTransfer_status_id(1);
            transfer.setMessage(message);

            try {
                HttpEntity<Transfer> entity = makeTransferEntity(transfer);
                ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "/request"  , HttpMethod.POST, entity, Transfer.class);
                transfer = response.getBody();
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
    }

    public Transfer[] viewTransferHistory (int id){
        Transfer[] transferList = new Transfer[]{};
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(baseUrl + "/" + id + "/history", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transferList = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if (transferList == null) {
            throw new NullPointerException("No Transfers found");
        } else {
            return transferList;
        }
    }

    public Transfer[] viewPendingTransfer(int id) {
        Transfer[] pendingTransfers = new Transfer[]{};
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(baseUrl + "/" + id + "/pending", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            pendingTransfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if (pendingTransfers == null) {
            throw new NullPointerException("No Transfers found");
        } else {
            return pendingTransfers;
        }
    }

    public boolean approveOrReject(int id, int action){
        Transfer transfer = viewTransferDetails(id);
        Account account = getAccountByUserId(transfer.getAccount_from() - 1000);
        boolean success = true;

        if (account.getBalance().compareTo(transfer.getAmount()) < 1) success = false;

        if (success) {
            try {
                ResponseEntity<Transfer> response =
                        restTemplate.exchange(baseUrl + "/" + id + "/pending/" + action, HttpMethod.PUT, makeAuthEntity(), Transfer.class);
                transfer = response.getBody();
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
        } else {
            System.out.println("Insufficient funds");
        }
        return success;
    }




    public Transfer viewTransferDetails(int id){
        Transfer transfer = null;
        try {
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(baseUrl + "/details/" + id, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if (transfer == null) {
            throw new NullPointerException("No Transfer found");
        } else {
            return transfer;
        }
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }

}
