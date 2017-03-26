package com.onewheelwizard.bakery;

import com.onewheelwizard.bakery.model.*;
import com.onewheelwizard.bakery.model.constants.WaterCondition;
import com.onewheelwizard.bakery.model.constants.WaterPurityCondition;
import com.onewheelwizard.bakery.model.constants.WaterType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.ZonedDateTime;
import java.util.Arrays;

@SpringBootApplication
public class BakeryApplication {

    public static void main(String[] args) {
        SpringApplication.run(BakeryApplication.class, args);
    }

    @Bean
    CommandLineRunner init(AccountRepository accountRepository,
                           WaterReportRepository waterReportRepository,
                           PurityReportRepository purityReportRepository) {
        return (evt) -> Arrays.asList("blake,david,zan,thor,dimitar,bob,sally,hello".split(","))
            .forEach(a -> {
                Account account = accountRepository.save(new Account(a, "pass", UserType.WORKER,a+"@pants.com","Mr.","Atlanta"));
                waterReportRepository.save(new WaterReport(account, ZonedDateTime.now(), 25, 2.31,
                        WaterType.BOTTLED, WaterCondition.POTABLE));
                waterReportRepository.save(new WaterReport(account, ZonedDateTime.now(), 25, 2.31,
                        WaterType.WELL, WaterCondition.TREATABLE_MUDDY));
                purityReportRepository.save(new PurityReport(account, ZonedDateTime.now(), 13.37,-13.37,
                        WaterPurityCondition.TREATABLE, 0.034f,0.23f));
            });
    }
}
