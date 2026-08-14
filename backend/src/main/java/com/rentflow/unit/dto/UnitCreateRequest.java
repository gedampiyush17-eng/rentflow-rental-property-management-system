package com.rentflow.unit.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UnitCreateRequest {

    @NotBlank(message = "Unit number is required")
    private String unitNumber;

    @NotNull(message = "Monthly rent is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Monthly rent must be greater than zero")
    private BigDecimal monthlyRent;

    @DecimalMin(value = "0.0", inclusive = true,
            message = "Security deposit cannot be negative")
    private BigDecimal securityDeposit;

    @DecimalMin(value = "0.0", inclusive = false,
            message = "Area must be greater than zero")
    private BigDecimal area;

    private String notes;

    @NotNull(message = "Property is required")
    private UUID propertyId;
}