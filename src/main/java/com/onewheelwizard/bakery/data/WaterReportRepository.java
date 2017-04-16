package com.onewheelwizard.bakery.data;

import com.onewheelwizard.bakery.model.WaterReport;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Collection;

public interface WaterReportRepository extends JpaRepository<WaterReport, Long> {
    Collection<WaterReport> findByAccountUsername(String username);

    @Transactional
    Long deleteAllByAccountUsername(String username);
}
