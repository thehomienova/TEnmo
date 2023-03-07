package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private AccountDao dao;

    private UserDao user;

    public AccountController(AccountDao accountDao, UserDao user) {
        this.dao = accountDao;
        this.user = user;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping (path = "/{id}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable int id){
        Account account = dao.getAccount(id);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        } else {
            return account;
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable int id) {
        User user = dao.getUser(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
        } else {
            return user;
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping (path = "/list/{id}", method = RequestMethod.GET)
    public User[] list (@PathVariable int id){
        List<User> accountList = dao.listUsers(id);
        if (accountList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        } else {
            return accountList.toArray(new User[accountList.size()]);
        }
    }


}
