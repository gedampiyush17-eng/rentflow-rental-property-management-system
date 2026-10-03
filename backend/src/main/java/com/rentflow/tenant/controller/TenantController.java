package com.rentflow.tenant.controller;

import com.rentflow.tenant.dto.TenantCreateRequest;
import com.rentflow.tenant.dto.request.TenantUpdateRequest;
import com.rentflow.tenant.dto.response.TenantResponse;
import com.rentflow.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Tenant",
        description = "Tenant Management APIs"
)
@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @Operation(summary = "Create a tenant")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantResponse createTenant(
            @Valid @RequestBody TenantCreateRequest request) {

        return tenantService.createTenant(request);
    }

    @Operation(summary = "Get all active tenants")
    @GetMapping
    public List<TenantResponse> getAllTenants() {

        return tenantService.getAllTenants();
    }

    @Operation(summary = "Get tenant by ID")
    @GetMapping("/{id}")
    public TenantResponse getTenantById(
            @PathVariable UUID id) {

        return tenantService.getTenantById(id);
    }

    @Operation(summary = "Update tenant")
    @PutMapping("/{id}")
    public TenantResponse updateTenant(
            @PathVariable UUID id,
            @Valid @RequestBody TenantUpdateRequest request) {

        return tenantService.updateTenant(id, request);
    }

    @Operation(summary = "Soft delete tenant")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenant(
            @PathVariable UUID id) {

        tenantService.deleteTenant(id);
    }
}