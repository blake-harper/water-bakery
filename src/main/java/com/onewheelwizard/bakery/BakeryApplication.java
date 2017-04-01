package com.onewheelwizard.bakery;

import com.onewheelwizard.bakery.model.Account;
import com.onewheelwizard.bakery.model.AccountRepository;
import com.onewheelwizard.bakery.model.PurityReport;
import com.onewheelwizard.bakery.model.PurityReportRepository;
import com.onewheelwizard.bakery.model.UserType;
import com.onewheelwizard.bakery.model.WaterReport;
import com.onewheelwizard.bakery.model.WaterReportRepository;
import com.onewheelwizard.bakery.model.constants.WaterCondition;
import com.onewheelwizard.bakery.model.constants.WaterPurityCondition;
import com.onewheelwizard.bakery.model.constants.WaterType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Random;

@SpringBootApplication
public class BakeryApplication {

    public static void main(String[] args) {
        SpringApplication.run(BakeryApplication.class, args);
    }

    @Bean
    CommandLineRunner init(AccountRepository accountRepository,
                           WaterReportRepository waterReportRepository,
                           PurityReportRepository purityReportRepository) {
        Random r = new Random(System.currentTimeMillis());
        return (evt) -> Arrays.asList("blake,david,zan,thor,dimitar,bob,sally,hello".split(","))
                .forEach(a -> {
                    Account account = accountRepository.save(new Account(a, "pass", UserType.WORKER, a + "@pants.com", "Mr.", "Atlanta"));
                    waterReportRepository.save(new WaterReport(account, ZonedDateTime.now(), r.nextInt(178) - 89 + r.nextDouble(), r.nextInt(358) - 179 + r.nextDouble(),
                            WaterType.BOTTLED, WaterCondition.POTABLE));
                    waterReportRepository.save(new WaterReport(account, ZonedDateTime.now(), r.nextInt(178) - 89 + r.nextDouble(), r.nextInt(358) - 179 + r.nextDouble(),
                            WaterType.WELL, WaterCondition.TREATABLE_MUDDY));
                    purityReportRepository.save(new PurityReport(account, ZonedDateTime.now(), r.nextInt(178) - 89 + r.nextDouble(), r.nextInt(358) - 179 + r.nextDouble(),
                            WaterPurityCondition.TREATABLE, r.nextFloat(), r.nextFloat()));
                });
    }
}
