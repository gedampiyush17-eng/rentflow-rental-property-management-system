package com.rentflow.receipt.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReceiptResponse {

    private UUID id;

    private String receiptNumber;

    private LocalDateTime issuedAt;

    private UUID paymentId;

    private BigDecimal amount;

    private String paymentMethod;

    private LocalDate paymentDate;

    private String transactionReference;

    private UUID rentCycleId;

    private Integer periodMonth;

    private Integer periodYear;

    private UUID tenantId;

    private UUID unitId;

    private UUID propertyId;
}