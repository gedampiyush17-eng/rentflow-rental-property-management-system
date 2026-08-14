package com.rentflow.lease.controller;

import com.rentflow.lease.dto.LeaseCreateRequest;
import com.rentflow.lease.dto.LeaseResponse;
import com.rentflow.lease.dto.LeaseUpdateRequest;
import com.rentflow.lease.service.LeaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leases")
@RequiredArgsConstructor
@Tag(
        name = "Lease",
        description = "Lease Management APIs"
)
public class LeaseController {

    private final LeaseService leaseService;

    @Operation(summary = "Create a new lease")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaseResponse createLease(
            @Valid @RequestBody LeaseCreateRequest request) {

        return leaseService.createLease(request);
    }

    @Operation(summary = "Get all active leases")
    @GetMapping
    public List<LeaseResponse> getAllLeases() {

        return leaseService.getAllLeases();
    }

    @Operation(summary = "Get lease by ID")
    @GetMapping("/{id}")
    public LeaseResponse getLeaseById(
            @PathVariable UUID id) {

        return leaseService.getLeaseById(id);
    }

    @Operation(summary = "Update lease")
    @PutMapping("/{id}")
    public LeaseResponse updateLease(
            @PathVariable UUID id,
            @Valid @RequestBody LeaseUpdateRequest request) {

        return leaseService.updateLease(
                id,
                request
        );
    }

    @Operation(summary = "Terminate an active lease")
    @PostMapping("/{id}/terminate")
    public LeaseResponse terminateLease(
            @PathVariable UUID id) {

        return leaseService.terminateLease(id);
    }

    @Operation(summary = "Expire an active lease")
    @PostMapping("/{id}/expire")
    public LeaseResponse expireLease(
            @PathVariable UUID id) {

        return leaseService.expireLease(id);
    }

    @Operation(summary = "Delete an inactive lease record")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLease(
            @PathVariable UUID id) {

        leaseService.deleteLease(id);
    }
}