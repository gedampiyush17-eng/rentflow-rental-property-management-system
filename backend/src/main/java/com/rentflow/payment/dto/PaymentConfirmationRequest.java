package com.rentflow.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentConfirmationRequest {

    @NotBlank(message = "Transaction reference is required")
    private String transactionReference;

    private String remarks;
}