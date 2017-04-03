package com.onewheelwizard.bakery.controller;

import com.onewheelwizard.bakery.model.Account;
import com.onewheelwizard.bakery.model.AccountRepository;
import com.onewheelwizard.bakery.model.WaterReport;
import com.onewheelwizard.bakery.model.WaterReportRepository;
import com.onewheelwizard.bakery.security.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Collection;

@RestController
public class WaterReportRestController {

    private final AccountRepository accountRepository;
    private final WaterReportRepository waterReportRepository;

    @Autowired
    public WaterReportRestController(AccountRepository accountRepository, WaterReportRepository waterReportRepository) {
        this.accountRepository = accountRepository;
        this.waterReportRepository = waterReportRepository;
    }

    //Create

    @RequestMapping(method = RequestMethod.POST, value = "/{username}/water-reports")
    WaterReport add(@PathVariable String username, @RequestBody WaterReport input) {
        validateUser(username);
        //TODO validate report

        return accountRepository.findByUsername(username)
                .map(account -> waterReportRepository.save(new WaterReport(account, ZonedDateTime.now(),
                        input.getLatitude(), input.getLongitude(), input.getWaterType(),
                        input.getWaterCondition())))
                .orElseThrow(() -> new InvalidReportException("water report"));
    }

    //Read

    @RequestMapping(method = RequestMethod.GET, value = "/water-reports")
    Collection<WaterReport> getReport() {
        return waterReportRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/water-reports/{id}")
    WaterReport getReport(@PathVariable Long id) {
        WaterReport report = waterReportRepository.findOne(id);
        if (report == null) {
            throw new IdNotFoundException(id);
        }
        return report;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{username}/water-reports")
    Collection<WaterReport> getReport(@PathVariable String username) {
        validateUser(username);
        return waterReportRepository.findByAccountUsername(username);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{username}/water-reports/{id}")
    WaterReport getReport(@PathVariable String username, @PathVariable Long id) {
        Account account = validateUser(username);
        WaterReport report = waterReportRepository.findOne(id);

        if (report == null || !report.getAccount().equals(account)) {
            throw new IdNotFoundException(id);
        }

        return report;
    }

    //Update - none

    //Delete

    @RequestMapping(method = RequestMethod.DELETE, value = "/water-reports/{id}")
    void delete(@PathVariable Long id) {
        WaterReport report = waterReportRepository.findOne(id);

        if (report == null) {
            throw new IdNotFoundException(id);
        }

        waterReportRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.DELETE, value ="{username}//water-reports/{id}")
    ResponseEntity<?> delete(@PathVariable String username, @PathVariable Long id) {
        //if this returns, then we know the id is valid and is paired with the correct user
        getReport(username, id);

        waterReportRepository.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Checks if the username is in the user repository
     *
     * @param username the username to return
     * @return the Account associated with the username
     * @throws UsernameNotFoundException if the username is not in the user repository
     */
    private Account validateUser(String username) {
        return accountRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );
    }
}
