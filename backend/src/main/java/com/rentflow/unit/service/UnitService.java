package com.rentflow.unit.service;

import com.rentflow.common.exception.ResourceNotFoundException;
import com.rentflow.property.entity.Property;
import com.rentflow.property.repository.PropertyRepository;
import com.rentflow.unit.dto.request.UnitCreateRequest;
import com.rentflow.unit.dto.request.UnitUpdateRequest;
import com.rentflow.unit.dto.response.UnitResponse;
import com.rentflow.unit.entity.Unit;
import com.rentflow.unit.enums.OccupancyStatus;
import com.rentflow.unit.mapper.UnitMapper;
import com.rentflow.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final UnitMapper unitMapper;
    private final PropertyRepository propertyRepository;

    public UnitResponse createUnit(UnitCreateRequest request) {

        Property property =
                propertyRepository.findByIdAndActiveTrue(
                        request.getPropertyId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Property not found with id: "
                                        + request.getPropertyId()
                        ));

        boolean exists =
                unitRepository
                        .existsByUnitNumberAndPropertyIdAndActiveTrue(
                                request.getUnitNumber(),
                                request.getPropertyId()
                        );

        if (exists) {
            throw new IllegalArgumentException(
                    "Unit number already exists in this property"
            );
        }

        Unit unit = unitMapper.toEntity(request);

        unit.setProperty(property);

        // Every new unit starts as vacant.
        unit.setOccupancyStatus(OccupancyStatus.VACANT);

        Unit savedUnit = unitRepository.save(unit);

        return unitMapper.toResponse(savedUnit);
    }

    public UnitResponse updateUnit(
            UUID id,
            UnitUpdateRequest request) {

        Unit unit =
                unitRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Unit not found with id: " + id
                                ));

        unitMapper.updateEntity(request, unit);

        Unit updatedUnit =
                unitRepository.save(unit);

        return unitMapper.toResponse(updatedUnit);
    }

    public void deleteUnit(UUID id) {

        Unit unit =
                unitRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Unit not found with id: " + id
                                ));

        /*
         * Do not allow deletion of an occupied unit.
         * Lease termination must happen first.
         */
        if (unit.getOccupancyStatus() == OccupancyStatus.OCCUPIED) {
            throw new IllegalStateException(
                    "Occupied unit cannot be deleted"
            );
        }

        unit.setActive(false);

        unitRepository.save(unit);
    }

    public List<UnitResponse> getAllUnits() {

        return unitRepository.findByActiveTrue()
                .stream()
                .map(unitMapper::toResponse)
                .toList();
    }

    public List<UnitResponse> getUnitsByProperty(
            UUID propertyId) {

        if (!propertyRepository
                .findByIdAndActiveTrue(propertyId)
                .isPresent()) {

            throw new ResourceNotFoundException(
                    "Property not found with id: " + propertyId
            );
        }

        return unitRepository
                .findByPropertyIdAndActiveTrue(propertyId)
                .stream()
                .map(unitMapper::toResponse)
                .toList();
    }

    public UnitResponse getUnitById(UUID id) {

        Unit unit =
                unitRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Unit not found with id: " + id
                                ));

        return unitMapper.toResponse(unit);
    }
}