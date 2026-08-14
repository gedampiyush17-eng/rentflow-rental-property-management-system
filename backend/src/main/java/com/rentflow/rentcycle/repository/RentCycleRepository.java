package com.rentflow.rentcycle.repository;

import com.rentflow.rentcycle.entity.RentCycle;
import com.rentflow.rentcycle.enums.RentCycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentCycleRepository extends JpaRepository<RentCycle, UUID> {

    List<RentCycle> findByActiveTrue();

    Optional<RentCycle> findByIdAndActiveTrue(UUID id);

    List<RentCycle> findByLeaseIdAndActiveTrue(UUID leaseId);

    List<RentCycle> findByStatusAndActiveTrue(RentCycleStatus status);

    Optional<RentCycle> findByLeaseIdAndPeriodStartAndPeriodEnd(UUID leaseId, LocalDate periodStart, LocalDate periodEnd);

    boolean existsByLeaseIdAndPeriodStartAndPeriodEnd(UUID leaseId, LocalDate periodStart, LocalDate periodEnd);

    long countByLeaseIdAndStatusAndActiveTrue(UUID leaseId, RentCycleStatus status);
}
