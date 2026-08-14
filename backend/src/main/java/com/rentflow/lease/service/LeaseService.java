package com.rentflow.lease.service;

import com.rentflow.common.exception.ResourceNotFoundException;
import com.rentflow.lease.dto.LeaseCreateRequest;
import com.rentflow.lease.dto.LeaseResponse;
import com.rentflow.lease.dto.LeaseUpdateRequest;
import com.rentflow.lease.entity.Lease;
import com.rentflow.lease.enums.LeaseStatus;
import com.rentflow.lease.mapper.LeaseMapper;
import com.rentflow.lease.repository.LeaseRepository;
import com.rentflow.tenant.entity.Tenant;
import com.rentflow.tenant.repository.TenantRepository;
import com.rentflow.unit.entity.Unit;
import com.rentflow.unit.enums.OccupancyStatus;
import com.rentflow.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final LeaseMapper leaseMapper;
    private final TenantRepository tenantRepository;
    private final UnitRepository unitRepository;

    /*
     * CREATE LEASE
     */
    public LeaseResponse createLease(
            LeaseCreateRequest request) {

        validateDates(
                request.getLeaseStartDate(),
                request.getLeaseEndDate()
        );

        Tenant tenant =
                tenantRepository
                        .findByIdAndActiveTrue(
                                request.getTenantId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant not found with id: "
                                                + request.getTenantId()
                                ));

        Unit unit =
                unitRepository
                        .findByIdAndActiveTrue(
                                request.getUnitId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Unit not found with id: "
                                                + request.getUnitId()
                                ));

        /*
         * A unit can have only one ACTIVE lease.
         */
        if (leaseRepository
                .existsByUnitIdAndLeaseStatus(
                        unit.getId(),
                        LeaseStatus.ACTIVE
                )) {

            throw new IllegalStateException(
                    "Unit already has an active lease"
            );
        }

        /*
         * A tenant can have only one ACTIVE lease
         * at a time.
         *
         * Historical leases are allowed.
         */
        if (leaseRepository
                .existsByTenantIdAndLeaseStatus(
                        tenant.getId(),
                        LeaseStatus.ACTIVE
                )) {

            throw new IllegalStateException(
                    "Tenant already has an active lease"
            );
        }

        /*
         * Unit must be vacant before a new lease starts.
         */
        if (unit.getOccupancyStatus()
                == OccupancyStatus.OCCUPIED) {

            throw new IllegalStateException(
                    "Unit is already occupied"
            );
        }

        Lease lease =
                leaseMapper.toEntity(request);

        lease.setTenant(tenant);
        lease.setUnit(unit);

        /*
         * Every newly created lease is ACTIVE.
         */
        lease.setLeaseStatus(LeaseStatus.ACTIVE);

        /*
         * Lease activation makes the unit occupied.
         */
        unit.setOccupancyStatus(
                OccupancyStatus.OCCUPIED
        );

        unitRepository.save(unit);

        Lease savedLease =
                leaseRepository.save(lease);

        return leaseMapper.toResponse(savedLease);
    }

    /*
     * GET ALL ACTIVE RECORDS
     */
    @Transactional(readOnly = true)
    public List<LeaseResponse> getAllLeases() {

        return leaseRepository
                .findByActiveTrue()
                .stream()
                .map(leaseMapper::toResponse)
                .toList();
    }

    /*
     * GET BY ID
     */
    @Transactional(readOnly = true)
    public LeaseResponse getLeaseById(UUID id) {

        Lease lease =
                leaseRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Lease not found with id: "
                                                + id
                                ));

        return leaseMapper.toResponse(lease);
    }

    /*
     * UPDATE LEASE
     */
    public LeaseResponse updateLease(
            UUID id,
            LeaseUpdateRequest request) {

        Lease lease =
                leaseRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Lease not found with id: "
                                                + id
                                ));

        validateDates(
                request.getLeaseStartDate(),
                request.getLeaseEndDate()
        );

        Tenant tenant =
                tenantRepository
                        .findByIdAndActiveTrue(
                                request.getTenantId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant not found with id: "
                                                + request.getTenantId()
                                ));

        Unit newUnit =
                unitRepository
                        .findByIdAndActiveTrue(
                                request.getUnitId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Unit not found with id: "
                                                + request.getUnitId()
                                ));

        /*
         * If changing tenant, make sure the new tenant
         * doesn't already have another active lease.
         */
        if (!lease.getTenant()
                .getId()
                .equals(tenant.getId())) {

            if (leaseRepository
                    .existsByTenantIdAndLeaseStatus(
                            tenant.getId(),
                            LeaseStatus.ACTIVE
                    )) {

                throw new IllegalStateException(
                        "Tenant already has an active lease"
                );
            }
        }

        /*
         * If changing unit, make sure the new unit
         * doesn't already have another active lease.
         */
        if (!lease.getUnit()
                .getId()
                .equals(newUnit.getId())) {

            if (leaseRepository
                    .existsByUnitIdAndLeaseStatus(
                            newUnit.getId(),
                            LeaseStatus.ACTIVE
                    )) {

                throw new IllegalStateException(
                        "Unit already has an active lease"
                );
            }

            if (newUnit.getOccupancyStatus()
                    == OccupancyStatus.OCCUPIED) {

                throw new IllegalStateException(
                        "New unit is already occupied"
                );
            }

            /*
             * Old unit becomes vacant.
             */
            Unit oldUnit = lease.getUnit();

            oldUnit.setOccupancyStatus(
                    OccupancyStatus.VACANT
            );

            unitRepository.save(oldUnit);

            /*
             * New unit becomes occupied.
             */
            newUnit.setOccupancyStatus(
                    OccupancyStatus.OCCUPIED
            );

            unitRepository.save(newUnit);
        }

        leaseMapper.updateEntity(request, lease);

        lease.setTenant(tenant);
        lease.setUnit(newUnit);

        Lease updatedLease =
                leaseRepository.save(lease);

        return leaseMapper.toResponse(updatedLease);
    }

    /*
     * TERMINATE LEASE
     */
    public LeaseResponse terminateLease(UUID id) {

        Lease lease =
                leaseRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Lease not found with id: "
                                                + id
                                ));

        if (lease.getLeaseStatus()
                != LeaseStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Only an active lease can be terminated"
            );
        }

        lease.setLeaseStatus(
                LeaseStatus.TERMINATED
        );

        /*
         * The unit becomes vacant.
         */
        Unit unit = lease.getUnit();

        unit.setOccupancyStatus(
                OccupancyStatus.VACANT
        );

        unitRepository.save(unit);

        Lease terminatedLease =
                leaseRepository.save(lease);

        return leaseMapper.toResponse(
                terminatedLease
        );
    }

    /*
     * EXPIRE LEASE
     *
     * This can later be called by a scheduler.
     */
    public LeaseResponse expireLease(UUID id) {

        Lease lease =
                leaseRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Lease not found with id: "
                                                + id
                                ));

        if (lease.getLeaseStatus()
                != LeaseStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Only an active lease can expire"
            );
        }

        lease.setLeaseStatus(
                LeaseStatus.EXPIRED
        );

        Unit unit = lease.getUnit();

        unit.setOccupancyStatus(
                OccupancyStatus.VACANT
        );

        unitRepository.save(unit);

        Lease expiredLease =
                leaseRepository.save(lease);

        return leaseMapper.toResponse(
                expiredLease
        );
    }

    /*
     * SOFT DELETE
     *
     * Historical lease records remain in the database.
     */
    public void deleteLease(UUID id) {

        Lease lease =
                leaseRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Lease not found with id: "
                                                + id
                                ));

        if (lease.getLeaseStatus()
                == LeaseStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Active lease cannot be deleted. "
                            + "Terminate the lease first."
            );
        }

        lease.setActive(false);

        leaseRepository.save(lease);
    }

    /*
     * DATE VALIDATION
     */
    private void validateDates(
            LocalDate startDate,
            LocalDate endDate) {

        if (!endDate.isAfter(startDate)) {

            throw new IllegalArgumentException(
                    "Lease end date must be after "
                            + "lease start date"
            );
        }
    }
}