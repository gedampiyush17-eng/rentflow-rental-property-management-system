package com.rentflow.receipt.service;

import com.rentflow.common.exception.ResourceNotFoundException;
import com.rentflow.payment.entity.Payment;
import com.rentflow.payment.enums.PaymentStatus;
import com.rentflow.payment.repository.PaymentRepository;
import com.rentflow.receipt.dto.ReceiptResponse;
import com.rentflow.receipt.entity.Receipt;
import com.rentflow.receipt.mapper.ReceiptMapper;
import com.rentflow.receipt.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;
    private final PaymentRepository paymentRepository;

    public ReceiptResponse generateReceipt(UUID paymentId) {

        Payment payment =
                paymentRepository
                        .findByIdAndActiveTrue(paymentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with id: "
                                                + paymentId
                                ));

        if (payment.getPaymentStatus()
                != PaymentStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Receipt can only be generated "
                            + "for a confirmed payment"
            );
        }

        Receipt existingReceipt =
                receiptRepository
                        .findByPaymentIdAndActiveTrue(paymentId)
                        .orElse(null);

        if (existingReceipt != null) {
            return receiptMapper.toResponse(
                    existingReceipt
            );
        }

        Receipt receipt = new Receipt();

        receipt.setPayment(payment);

        receipt.setReceiptNumber(
                generateReceiptNumber()
        );

        receipt.setIssuedAt(
                LocalDateTime.now()
        );

        Receipt savedReceipt =
                receiptRepository.save(receipt);

        return receiptMapper.toResponse(savedReceipt);
    }

    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptById(
            UUID id) {

        Receipt receipt =
                receiptRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Receipt not found with id: "
                                                + id
                                ));

        return receiptMapper.toResponse(receipt);
    }

    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptByPayment(
            UUID paymentId) {

        Receipt receipt =
                receiptRepository
                        .findByPaymentIdAndActiveTrue(
                                paymentId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Receipt not found for payment: "
                                                + paymentId
                                ));

        return receiptMapper.toResponse(receipt);
    }

    private String generateReceiptNumber() {

        return "RF-"
                + System.currentTimeMillis();
    }
}