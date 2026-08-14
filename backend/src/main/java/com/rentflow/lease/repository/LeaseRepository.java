package com.rentflow.lease.repository;

import com.rentflow.lease.entity.Lease;
import com.rentflow.lease.enums.LeaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaseRepository
        extends JpaRepository<Lease, UUID> {

    List<Lease> findByActiveTrue();

    Optional<Lease> findByIdAndActiveTrue(UUID id);

    boolean existsByUnitIdAndLeaseStatus(
            UUID unitId,
            LeaseStatus leaseStatus
    );

    boolean existsByTenantIdAndLeaseStatus(
            UUID tenantId,
            LeaseStatus leaseStatus
    );

    Optional<Lease> findByUnitIdAndLeaseStatus(
            UUID unitId,
            LeaseStatus leaseStatus
    );

    List<Lease> findByTenantIdAndActiveTrue(UUID tenantId);

    List<Lease> findByUnitIdAndActiveTrue(UUID unitId);
}