package com.rentflow.rentcycle.dto;

import com.rentflow.rentcycle.enums.RentCycleStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RentCycleResponse {

    private UUID id;

    private UUID leaseId;

    private UUID tenantId;

    private UUID unitId;

    private LocalDate periodStart;

    private LocalDate periodEnd;

    private LocalDate dueDate;

    private BigDecimal amountDue;

    private BigDecimal amountPaid;

    private BigDecimal balanceDue;

    private RentCycleStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}