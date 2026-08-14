package com.rentflow.unit.controller;

import com.rentflow.unit.dto.request.UnitCreateRequest;
import com.rentflow.unit.dto.request.UnitUpdateRequest;
import com.rentflow.unit.dto.response.UnitResponse;
import com.rentflow.unit.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Unit",
        description = "Unit Management APIs"
)
@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @Operation(summary = "Create a new unit")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnitResponse createUnit(
            @Valid @RequestBody UnitCreateRequest request) {

        return unitService.createUnit(request);
    }

    @Operation(summary = "Get all active units")
    @GetMapping
    public List<UnitResponse> getAllUnits() {

        return unitService.getAllUnits();
    }

    @Operation(summary = "Get units by property")
    @GetMapping("/property/{propertyId}")
    public List<UnitResponse> getUnitsByProperty(
            @PathVariable UUID propertyId) {

        return unitService.getUnitsByProperty(propertyId);
    }

    @Operation(summary = "Get unit by ID")
    @GetMapping("/{id}")
    public UnitResponse getUnitById(
            @PathVariable UUID id) {

        return unitService.getUnitById(id);
    }

    @Operation(summary = "Update unit")
    @PutMapping("/{id}")
    public UnitResponse updateUnit(
            @PathVariable UUID id,
            @Valid @RequestBody UnitUpdateRequest request) {

        return unitService.updateUnit(id, request);
    }

    @Operation(summary = "Soft delete unit")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUnit(
            @PathVariable UUID id) {

        unitService.deleteUnit(id);
    }
}