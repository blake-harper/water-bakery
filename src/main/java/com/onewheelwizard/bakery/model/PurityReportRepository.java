package com.onewheelwizard.bakery.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface PurityReportRepository extends JpaRepository<PurityReport, Long> {
    Collection<PurityReport> findByAccountUsername(String username);
    Optional<PurityReport> findById(Long id);
}
