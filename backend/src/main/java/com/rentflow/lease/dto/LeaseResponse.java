package com.rentflow.lease.dto;

import com.rentflow.lease.enums.LeaseStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LeaseResponse {

    private UUID id;

    private LocalDate leaseStartDate;

    private LocalDate leaseEndDate;

    private BigDecimal monthlyRent;

    private BigDecimal securityDeposit;

    private Integer paymentDueDay;

    private LeaseStatus leaseStatus;

    private UUID tenantId;

    private UUID unitId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}