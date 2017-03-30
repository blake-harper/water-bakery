package com.onewheelwizard.bakery.controller;

import com.onewheelwizard.bakery.model.*;
import com.onewheelwizard.bakery.security.UsernameNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
public class AccountRestController {
    private final AccountRepository accountRepository;
    private final WaterReportRepository waterReportRepository;
    private final PurityReportRepository purityReportRepository;

    public AccountRestController(AccountRepository accountRepository,
                                 WaterReportRepository waterReportRepository,
                                 PurityReportRepository purityReportRepository) {
        this.accountRepository = accountRepository;
        this.waterReportRepository = waterReportRepository;
        this.purityReportRepository = purityReportRepository;
    }

    //Create

    @RequestMapping(method = RequestMethod.POST, value = "/accounts")
    ResponseEntity<?> create(@RequestBody Account account) {
        Optional<Account> existingAccount = accountRepository.findByUsername(account.getUsername());

        if (existingAccount.isPresent()) {
            throw new UsernameAlreadyExistsException(account.getUsername());
        }

        //TODO validation
        Account createdAccount = accountRepository.save(new Account(account.getUsername(), account.getPassword(),
                account.getUserType(), account.getEmail(), account.getTitle(), account.getCity()));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{username}").
                buildAndExpand(createdAccount.getUsername()).toUri();

        return ResponseEntity.created(uri).build();
    }

    //Read

    @RequestMapping(method = RequestMethod.GET, value = "/accounts/{username}")
    Account getAccountByUsername(@PathVariable String username) {
        return accountRepository.findByUsername(username).orElseThrow(
                ()-> new UsernameNotFoundException(username)
        );
    }

    //Update

    @RequestMapping(method = RequestMethod.PUT, value = "/accounts/{username}")
    ResponseEntity<?> createOrUpdate(@PathVariable String username,@RequestBody Account account) {
        Optional<Account> existingAccount = accountRepository.findByUsername(username);
        Account target;
        if (existingAccount.isPresent()) {
            target = existingAccount.get();
            if (account.getUserType() != null) {
                target.setUserType(account.getUserType());
            }
            if (account.getEmail() != null) {
                target.setEmail(account.getEmail());
            }
            if (account.getCity() != null) {
                target.setCity(account.getCity());
            }
            if (account.getTitle() != null) {
                target.setTitle(account.getTitle());
            }
            if (account.getPassword() != null) {
                //TODO some kind of "check old password before updating". maybe in the header?
                target.setPassword(account.getPassword());
            }

            //TODO validation

            accountRepository.save(target);

        } else {
            //NB username should always be from the request URL
            target = new Account(username, account.getPassword(),account.getUserType(),account.getEmail(),
                    account.getTitle(),account.getCity());

            //TODO validation

            accountRepository.save(target);
        }


        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();

        return ResponseEntity.noContent().build();

    }

    //Delete

    @RequestMapping(method=RequestMethod.DELETE, value="/accounts/{username}")
    public ResponseEntity<?> delete(@PathVariable String username) {
        Account account = accountRepository.findByUsername(username).orElseThrow(
                    ()-> new UsernameNotFoundException(username)
                );

        waterReportRepository.delete(account.getWaterReports());
        purityReportRepository.delete(account.getPurityReports());

        accountRepository.deleteByUsername(username);

        return ResponseEntity.noContent().build();
    }

}
