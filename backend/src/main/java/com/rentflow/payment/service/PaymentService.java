package com.rentflow.payment.service;

import com.rentflow.auth.entity.User;
import com.rentflow.auth.repository.UserRepository;
import com.rentflow.common.exception.ResourceNotFoundException;
import com.rentflow.payment.dto.PaymentConfirmationRequest;
import com.rentflow.payment.dto.request.PaymentCreateRequest;
import com.rentflow.payment.dto.response.PaymentResponse;
import com.rentflow.payment.entity.Payment;
import com.rentflow.payment.enums.PaymentMethod;
import com.rentflow.payment.enums.PaymentStatus;
import com.rentflow.payment.mapper.PaymentMapper;
import com.rentflow.payment.repository.PaymentRepository;
import com.rentflow.rentcycle.entity.RentCycle;
import com.rentflow.rentcycle.enums.RentCycleStatus;
import com.rentflow.rentcycle.repository.RentCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RentCycleRepository rentCycleRepository;
    private final UserRepository userRepository;

    /*
     * Record and confirm a payment.
     *
     * Only OWNER / MANAGER should reach this operation.
     */
    public PaymentResponse createPayment(
            PaymentCreateRequest request) {

        User currentUser = getCurrentUser();

        validatePaymentRecorder(currentUser);

        RentCycle rentCycle =
                rentCycleRepository
                        .findByIdAndActiveTrue(
                                request.getRentCycleId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rent cycle not found with id: "
                                                + request.getRentCycleId()
                                ));

        /*
         * Payment cannot be recorded against a cycle
         * that has already been fully paid.
         */
        if (rentCycle.getStatus()
                == RentCycleStatus.PAID) {

            throw new IllegalStateException(
                    "Rent cycle is already fully paid"
            );
        }

        BigDecimal balanceDue =
                rentCycle.getBalanceDue();

        /*
         * Payment cannot be greater than outstanding balance.
         */
        if (request.getAmount()
                .compareTo(balanceDue) > 0) {

            throw new IllegalArgumentException(
                    "Payment amount cannot exceed "
                            + "the outstanding balance of "
                            + balanceDue
            );
        }

        /*
         * UPI payment should have a transaction reference.
         */
        if (request.getPaymentMethod()
                == PaymentMethod.UPI
                && (request.getTransactionReference()
                == null
                || request.getTransactionReference()
                .isBlank())) {

            throw new IllegalArgumentException(
                    "Transaction reference is required "
                            + "for UPI payment"
            );
        }

        /*
         * CASH should not reuse a UPI transaction reference.
         */
        if (request.getPaymentMethod()
                == PaymentMethod.CASH
                && request.getTransactionReference()
                != null
                && !request.getTransactionReference()
                .isBlank()) {

            throw new IllegalArgumentException(
                    "Transaction reference should be empty "
                            + "for cash payment"
            );
        }

        /*
         * Prevent duplicate UPI transaction references.
         */
        if (request.getTransactionReference()
                != null
                && !request.getTransactionReference()
                .isBlank()
                && paymentRepository
                .existsByTransactionReference(
                        request.getTransactionReference()
                )) {

            throw new IllegalArgumentException(
                    "Transaction reference already exists"
            );
        }

        Payment payment =
                paymentMapper.toEntity(request);

        payment.setRentCycle(rentCycle);

        /*
         * Phase 1 uses trusted manual confirmation.
         *
         * Owner/Manager records the payment only after
         * verifying the cash/UPI payment.
         */
        payment.setPaymentStatus(
                PaymentStatus.CONFIRMED
        );

        payment.setConfirmedBy(currentUser);

        payment.setConfirmedAt(
                LocalDateTime.now()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        /*
         * Update RentCycle amounts.
         */
        BigDecimal newAmountPaid =
                rentCycle.getAmountPaid()
                        .add(request.getAmount());

        BigDecimal newBalance =
                rentCycle.getAmountDue()
                        .subtract(newAmountPaid);

        rentCycle.setAmountPaid(newAmountPaid);

        rentCycle.setBalanceDue(newBalance);

        /*
         * Determine the new RentCycle status.
         */
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {

            rentCycle.setStatus(
                    RentCycleStatus.PAID
            );

        } else {

            rentCycle.setStatus(
                    RentCycleStatus.PARTIALLY_PAID
            );
        }

        rentCycleRepository.save(rentCycle);

        return paymentMapper.toResponse(savedPayment);
    }

    /*
     * Confirm an existing pending payment.
     *
     * This method is useful if we later introduce a
     * "Tenant says I have paid" workflow.
     */
    public PaymentResponse confirmPayment(
            UUID paymentId,
            PaymentConfirmationRequest request) {

        User currentUser = getCurrentUser();

        validatePaymentRecorder(currentUser);

        Payment payment =
                paymentRepository
                        .findByIdAndActiveTrue(paymentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with id: "
                                                + paymentId
                                ));

        if (payment.getPaymentStatus()
                == PaymentStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Payment is already confirmed"
            );
        }

        if (request.getTransactionReference() != null
                && !request.getTransactionReference()
                .isBlank()) {

            if (paymentRepository
                    .existsByTransactionReference(
                            request.getTransactionReference()
                    )) {

                throw new IllegalArgumentException(
                        "Transaction reference already exists"
                );
            }

            payment.setTransactionReference(
                    request.getTransactionReference()
            );
        }

        payment.setRemarks(
                request.getRemarks()
        );

        payment.setPaymentStatus(
                PaymentStatus.CONFIRMED
        );

        payment.setConfirmedBy(
                currentUser
        );

        payment.setConfirmedAt(
                LocalDateTime.now()
        );

        Payment confirmedPayment =
                paymentRepository.save(payment);

        return paymentMapper.toResponse(
                confirmedPayment
        );
    }

    /*
     * Get all active payments.
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {

        return paymentRepository
                .findByActiveTrue()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    /*
     * Get payment by ID.
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            UUID id) {

        Payment payment =
                paymentRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with id: "
                                                + id
                                ));

        return paymentMapper.toResponse(payment);
    }

    /*
     * Get all payments for a rent cycle.
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByRentCycle(
            UUID rentCycleId) {

        if (!rentCycleRepository
                .findByIdAndActiveTrue(rentCycleId)
                .isPresent()) {

            throw new ResourceNotFoundException(
                    "Rent cycle not found with id: "
                            + rentCycleId
            );
        }

        return paymentRepository
                .findByRentCycleIdAndActiveTrue(
                        rentCycleId
                )
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    /*
     * Get currently authenticated User.
     */
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        ));
    }

    /*
     * Only OWNER and MANAGER can record/confirm
     * payments according to Phase 1 rules.
     */
    private void validatePaymentRecorder(
            User user) {

        String role =
                user.getRole().name();

        if (!role.equals("OWNER")
                && !role.equals("MANAGER")) {

            throw new SecurityException(
                    "Only Owner or Property Manager "
                            + "can record or confirm payments"
            );
        }
    }
}