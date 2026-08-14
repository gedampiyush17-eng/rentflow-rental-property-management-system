package com.rentflow.dashboard.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardResponse {

    // Property summary
    private long totalProperties;
    private long activeProperties;

    // Unit summary
    private long totalUnits;
    private long occupiedUnits;
    private long vacantUnits;

    // Lease summary
    private long activeLeases;
    private long expiredLeases;

    // Rent summary
    private BigDecimal totalRentDue;
    private BigDecimal totalCollected;
    private BigDecimal totalOutstanding;
    private BigDecimal totalOverdue;

    // Payment summary
    private long totalPayments;
    private long cashPayments;
    private long upiPayments;
}