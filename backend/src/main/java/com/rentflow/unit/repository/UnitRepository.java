package com.rentflow.unit.repository;

import com.rentflow.unit.entity.Unit;
import com.rentflow.unit.enums.OccupancyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnitRepository extends JpaRepository<Unit, UUID> {

    List<Unit> findByActiveTrue();

    Optional<Unit> findByIdAndActiveTrue(UUID id);

    List<Unit> findByPropertyIdAndActiveTrue(UUID propertyId);

    Optional<Unit> findByIdAndPropertyIdAndActiveTrue(
            UUID id,
            UUID propertyId
    );

    boolean existsByUnitNumberAndPropertyIdAndActiveTrue(
            String unitNumber,
            UUID propertyId
    );

    long countByPropertyIdAndActiveTrue(UUID propertyId);

    long countByPropertyIdAndOccupancyStatusAndActiveTrue(
            UUID propertyId,
            OccupancyStatus occupancyStatus
    );
}