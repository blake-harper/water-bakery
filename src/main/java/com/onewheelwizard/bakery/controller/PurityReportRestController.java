package com.onewheelwizard.bakery.controller;

import com.onewheelwizard.bakery.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collection;

@RestController
public class PurityReportRestController {
    private final AccountRepository accountRepository;
    private final PurityReportRepository purityReportRepository;

    @Autowired
    public PurityReportRestController(AccountRepository accountRepository, PurityReportRepository purityReportRepository) {
        this.accountRepository = accountRepository;
        this.purityReportRepository = purityReportRepository;
    }

    //Create

    @RequestMapping(method = RequestMethod.POST, value = "/{username}/purity-reports")
    ResponseEntity<?> add(@PathVariable String username, @RequestBody PurityReport input) {
        validateUser(username);
        //TODO validate report

        return accountRepository.findByUsername(username)
                .map(account-> {
                    PurityReport result = purityReportRepository.save(new PurityReport(account, ZonedDateTime.now(),
                            input.getLatitude(),input.getLongitude(), input.getWaterPurityCondition(),
                            input.getVirusPPM(),input.getContaminantPPM()));
                    URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{id}").buildAndExpand(result.getId()).toUri();

                    return ResponseEntity.created(uri).build();
                })
                .orElse(ResponseEntity.noContent().build());
    }

    //Read

    @RequestMapping(method = RequestMethod.GET, value ="/purity-reports")
    Collection<PurityReport> getPurityReports() {
        return purityReportRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value ="/purity-reports/{id}")
    PurityReport getReport(@PathVariable Long id) {
        PurityReport report = purityReportRepository.findOne(id);
        if (report == null) {
            throw new IdNotFoundException(id);
        }
        return report;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{username}/purity-reports")
    Collection<PurityReport> getPurityReports(@PathVariable String username) {
        validateUser(username);
        return purityReportRepository.findByAccountUsername(username);
    }

    @RequestMapping(method = RequestMethod.GET, value ="/{username}/purity-reports/{id}")
    PurityReport getReport(@PathVariable String username, @PathVariable Long id) {
        Account account = validateUser(username);
        PurityReport purityReport = purityReportRepository.findOne(id);

        if (purityReport == null || !purityReport.getAccount().equals(account)) {
            throw new IdNotFoundException(id);
        }

        return purityReport;
    }

    //Update - none

    //Delete

    @RequestMapping(method = RequestMethod.DELETE, value ="/purity-reports/{id}")
    void delete(@PathVariable Long id) {
        PurityReport report = purityReportRepository.findOne(id);

        if (report == null) {
            throw new IdNotFoundException(id);
        }

        purityReportRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.DELETE, value ="/{username}/purity-reports/{id}")
    void delete(@PathVariable String username, @PathVariable Long id) {
        //if this returns, then we know the id is valid and is paired with the correct user
        getReport(username, id);

        purityReportRepository.delete(id);
    }

    /**
     * Checks if the username is in the user repository
     * @param username the username to return
     * @return the Account associated with the username
     * @throws UsernameNotFoundException if the username is not in the user repository
     */
    private Account validateUser(String username) {
        return accountRepository.findByUsername(username).orElseThrow(
                ()-> new UsernameNotFoundException(username)
        );
    }
}
