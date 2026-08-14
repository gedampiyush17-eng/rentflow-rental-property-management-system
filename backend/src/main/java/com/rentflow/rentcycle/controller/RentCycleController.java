package com.rentflow.rentcycle.controller;

import com.rentflow.rentcycle.dto.RentCycleResponse;
import com.rentflow.rentcycle.service.RentCycleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rent-cycles")
@RequiredArgsConstructor
@Tag(
        name = "Rent Cycle",
        description = "Rent Cycle Management APIs"
)
public class RentCycleController {

    private final RentCycleService rentCycleService;

    @Operation(
            summary = "Generate rent cycles for a lease"
    )
    @PostMapping("/generate/{leaseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public List<RentCycleResponse> generateCycles(
            @PathVariable UUID leaseId) {

        return rentCycleService.generateCycles(leaseId);
    }

    @Operation(
            summary = "Get all active rent cycles"
    )
    @GetMapping
    public List<RentCycleResponse> getAllCycles() {

        return rentCycleService.getAllCycles();
    }

    @Operation(
            summary = "Get rent cycle by ID"
    )
    @GetMapping("/{id}")
    public RentCycleResponse getCycleById(
            @PathVariable UUID id) {

        return rentCycleService.getCycleById(id);
    }

    @Operation(
            summary = "Get rent cycles by lease"
    )
    @GetMapping("/lease/{leaseId}")
    public List<RentCycleResponse> getCyclesByLease(
            @PathVariable UUID leaseId) {

        return rentCycleService.getCyclesByLease(leaseId);
    }

    @Operation(
            summary = "Delete rent cycle"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCycle(
            @PathVariable UUID id) {

        rentCycleService.deleteCycle(id);
    }
}