package com.onewheelwizard.bakery.data;

import com.onewheelwizard.bakery.model.PurityReport;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

public interface PurityReportRepository extends JpaRepository<PurityReport, Long> {
    Collection<PurityReport> findByAccountUsername(String username);

    Optional<PurityReport> findById(Long id);

    @Transactional
    Long deleteAllByAccountUsername(String username);
}
