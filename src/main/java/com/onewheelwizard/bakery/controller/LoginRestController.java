package com.onewheelwizard.bakery.controller;

import com.onewheelwizard.bakery.model.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginRestController {
    private final AccountRepository accountRepository;

    @Autowired
    public LoginRestController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value ="/login")
    ResponseEntity<?> verifyLogin() {
        //if we can even see this, then we know the login was valid
        return ResponseEntity.noContent().build();
    }
}
