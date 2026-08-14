package com.rentflow.receipt.repository;

import com.rentflow.receipt.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {
    Optional<Receipt> findByIdAndActiveTrue(UUID id);

    Optional<Receipt> findByPaymentIdAndActiveTrue(UUID paymentId);

    Optional<Receipt> findByReceiptNumber(String receiptNumber);

    boolean existsByPaymentId(UUID paymentId);

    boolean existsByReceiptNumber(String receiptNumber);

}
