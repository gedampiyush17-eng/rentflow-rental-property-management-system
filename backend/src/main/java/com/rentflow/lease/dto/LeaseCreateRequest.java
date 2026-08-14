package com.rentflow.lease.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class LeaseCreateRequest {

    @NotNull(message = "Lease start date is required")
    private LocalDate leaseStartDate;

    @NotNull(message = "Lease end date is required")
    private LocalDate leaseEndDate;

    @NotNull(message = "Monthly rent is required")
    @Positive(message = "Monthly rent must be greater than zero")
    private BigDecimal monthlyRent;

    @NotNull(message = "Security deposit is required")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Security deposit cannot be negative"
    )
    private BigDecimal securityDeposit;

    @NotNull(message = "Payment due day is required")
    @Min(
            value = 1,
            message = "Payment due day must be between 1 and 28"
    )
    @Max(
            value = 28,
            message = "Payment due day must be between 1 and 28"
    )
    private Integer paymentDueDay;

    @NotNull(message = "Tenant is required")
    private UUID tenantId;

    @NotNull(message = "Unit is required")
    private UUID unitId;
}