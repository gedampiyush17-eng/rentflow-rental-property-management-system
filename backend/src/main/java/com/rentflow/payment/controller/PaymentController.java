package com.rentflow.payment.controller;

import com.rentflow.payment.dto.PaymentConfirmationRequest;
import com.rentflow.payment.dto.request.PaymentCreateRequest;
import com.rentflow.payment.dto.response.PaymentResponse;
import com.rentflow.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(
        name = "Payment",
        description = "Rent Payment Management APIs"
)
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Record a new payment"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(
            @Valid @RequestBody PaymentCreateRequest request) {

        return paymentService.createPayment(request);
    }

    @Operation(
            summary = "Get all payments"
    )
    @GetMapping
    public List<PaymentResponse> getAllPayments() {

        return paymentService.getAllPayments();
    }

    @Operation(
            summary = "Get payment by ID"
    )
    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(
            @PathVariable UUID id) {

        return paymentService.getPaymentById(id);
    }

    @Operation(summary = "Get payments for a rent cycle")
    @GetMapping("/rent-cycle/{rentCycleId}")
    public List<PaymentResponse> getPaymentsByRentCycle(
            @PathVariable UUID rentCycleId) {

        return paymentService.getPaymentsByRentCycle(
                rentCycleId
        );
    }

    @Operation(
            summary = "Confirm a pending payment"
    )
    @PostMapping("/{id}/confirm")
    public PaymentResponse confirmPayment(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentConfirmationRequest request) {

        return paymentService.confirmPayment(id, request);
    }
}