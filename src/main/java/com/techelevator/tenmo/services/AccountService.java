package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }
    public AccountService(String url) {
        this.baseUrl = url;
    }


    public User getUserByAccountId(int id) {
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
    public User[] listUsers(int id){
        User[] userList = null;
        try {
            ResponseEntity<User[]> response =
                    restTemplate.exchange(baseUrl + "list/" + id, HttpMethod.GET, makeAuthEntity(), User[].class);
            userList = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if (userList == null) {
            throw new NullPointerException("No Users found");
        } else {
            return userList;
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
        if (account == null) {
            throw new NullPointerException("Account Not Found");
        } else {
            return account;
        }
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }
}
