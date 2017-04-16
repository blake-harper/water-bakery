package com.onewheelwizard.bakery.controller;

import com.onewheelwizard.bakery.model.Account;
import com.onewheelwizard.bakery.data.AccountRepository;
import com.onewheelwizard.bakery.model.PurityReport;
import com.onewheelwizard.bakery.data.PurityReportRepository;
import com.onewheelwizard.bakery.model.WaterReport;
import com.onewheelwizard.bakery.data.WaterReportRepository;
import com.onewheelwizard.bakery.model.constants.UserType;
import com.onewheelwizard.bakery.model.constants.WaterCondition;
import com.onewheelwizard.bakery.model.constants.WaterPurityCondition;
import com.onewheelwizard.bakery.model.constants.WaterType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Random;

@RestController
class ResetRestController {

    private AccountRepository accountRepository;
    private WaterReportRepository waterReportRepository;
    private PurityReportRepository purityReportRepository;

    public ResetRestController(AccountRepository accountRepository,
            WaterReportRepository waterReportRepository,
            PurityReportRepository purityReportRepository) {
        this.accountRepository = accountRepository;
        this.waterReportRepository = waterReportRepository;
        this.purityReportRepository = purityReportRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reset")
    void reinitializeDatabase() {
        purityReportRepository.deleteAll();
        waterReportRepository.deleteAll();
        accountRepository.deleteAll();

        Random r = new Random(System.currentTimeMillis());
        Arrays.asList("blake,david,zan,thor,dimitar".split(","))
                .forEach(a -> {
                    double latitude = r.nextInt(178) - 89 + r.nextDouble();
                    double longitude = r.nextInt(358) - 175 + r.nextDouble();
                    Account account = accountRepository
                            .save(new Account(a, a + "1", UserType.WORKER, a + "@67.co", "The honorable", "Atlanta"));
                    waterReportRepository.save(new WaterReport(account, ZonedDateTime.now(), r.nextInt(178) - 89 + r.nextDouble(), r.nextInt(358) - 179 + r.nextDouble(),
                            WaterType.values()[r.nextInt(WaterType.values().length)], WaterCondition.values()[r.nextInt(WaterCondition.values().length)]));
                    waterReportRepository.save(new WaterReport(account, ZonedDateTime.now(), r.nextInt(178) - 89 + r.nextDouble(), r.nextInt(358) - 179 + r.nextDouble(),
                            WaterType.values()[r.nextInt(WaterType.values().length)], WaterCondition.values()[r.nextInt(WaterCondition.values().length)]));
                    waterReportRepository.save(new WaterReport(account, ZonedDateTime.now(), r.nextInt(178) - 89 + r.nextDouble(), r.nextInt(358) - 179 + r.nextDouble(),
                            WaterType.values()[r.nextInt(WaterType.values().length)], WaterCondition.values()[r.nextInt(WaterCondition.values().length)]));
                    for (int i = 1; i < 13; i++) {
                        purityReportRepository
                                .save(new PurityReport(account, ZonedDateTime.of(2016, i, 13, 3, 33, 37, 357,
                                        ZoneId.systemDefault()), latitude, longitude, WaterPurityCondition.values()[r.nextInt(WaterPurityCondition.values().length)],
                                        r.nextFloat() * r.nextInt(10), r.nextFloat()));
                    }
                    purityReportRepository
                            .save(new PurityReport(account, ZonedDateTime.of(2016, r.nextInt(12)+1, 10, 10, 9, 2, 1,
                                    ZoneId.systemDefault()), latitude, longitude, WaterPurityCondition.values()[r.nextInt(WaterPurityCondition.values().length)],
                                    r.nextFloat() * r.nextInt(10), r.nextFloat()));
                });
        accountRepository
                .save(new Account("user", "user", UserType.CONTRIBUTOR, "user@user.ca", "The lowly", "Atlanta"));
        accountRepository
                .save(new Account("manager", "manager", UserType.MANAGER, "user@user.ca", "The regal", "Atlanta"));
    }
}
