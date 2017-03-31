package com.onewheelwizard.bakery.controller;

import com.onewheelwizard.bakery.model.Account;
import com.onewheelwizard.bakery.model.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class LoginRestController {
    private final AccountRepository accountRepository;

    @Autowired
    public LoginRestController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    ResponseEntity<?> verifyAccount(@RequestBody Credentials credentials) {
        Optional<Account> userAccount = accountRepository.findByUsername(credentials.getUsername());

        return userAccount.map(
                existingAccount -> existingAccount.getPassword().equals(credentials.getPassword())
                        ? ResponseEntity.noContent().build()
                        : new ResponseEntity<String>("Unauthorized",HttpStatus.UNAUTHORIZED))
                .orElse(ResponseEntity.notFound().build());
    }


    static class Credentials {
        private String username = null;
        private String password = null;

        Credentials() {
        }

        Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        String getUsername() {
            return username;
        }

        void setUsername(String username) {
            this.username = username;
        }

        String getPassword() {
            return password;
        }

        void setPassword(String password) {
            this.password = password;
        }
    }
}
