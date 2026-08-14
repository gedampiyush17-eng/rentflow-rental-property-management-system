package com.rentflow.unit.dto.response;

import com.rentflow.unit.enums.OccupancyStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UnitResponse {

    private UUID id;

    private String unitNumber;

    private BigDecimal monthlyRent;

    private BigDecimal securityDeposit;

    private BigDecimal area;

    private OccupancyStatus occupancyStatus;

    private String notes;

    private UUID propertyId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}