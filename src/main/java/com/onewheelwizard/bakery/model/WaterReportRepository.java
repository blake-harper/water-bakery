package com.onewheelwizard.bakery.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface WaterReportRepository extends JpaRepository<WaterReport, Long> {
    Collection<WaterReport> findByAccountUsername(String username);
}
