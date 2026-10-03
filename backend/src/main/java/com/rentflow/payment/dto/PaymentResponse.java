package com.rentflow.payment.dto;

import com.rentflow.payment.enums.PaymentMethod;
import com.rentflow.payment.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentResponse {

    private UUID id;

    private UUID rentCycleId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String transactionReference;

    private UUID confirmedBy;

    private LocalDateTime confirmedAt;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}