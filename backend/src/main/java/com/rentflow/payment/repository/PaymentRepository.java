package com.rentflow.payment.repository;

import com.rentflow.payment.entity.Payment;
import com.rentflow.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByActiveTrue();

    Optional<Payment> findByIdAndActiveTrue(UUID id);

    List<Payment> findByRentCycleIdAndActiveTrue(UUID rentCycleId);

    List<Payment> findByPaymentStatusAndActiveTrue(PaymentStatus paymentStatus);

    Optional<Payment> findByTransactionReference(String transactionReference);

    boolean existsByTransactionReference(String transactionReference);

    long countByPaymentStatusAndActiveTrue(PaymentStatus paymentStatus);

}
