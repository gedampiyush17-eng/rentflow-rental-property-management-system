package com.rentflow.payment.service;

import com.rentflow.auth.entity.User;
import com.rentflow.auth.enums.Role;
import com.rentflow.auth.repository.UserRepository;
import com.rentflow.common.exception.ResourceNotFoundException;
import com.rentflow.payment.dto.PaymentConfirmationRequest;
import com.rentflow.payment.dto.PaymentCreateRequest;
import com.rentflow.payment.dto.PaymentSimulationRequest;
import com.rentflow.payment.dto.PaymentResponse;
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
     * =========================================================
     * MANUAL PAYMENT
     * =========================================================
     *
     * Used by OWNER / MANAGER when they have already verified
     * a real cash/UPI payment.
     *
     * This payment is immediately CONFIRMED.
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

        validateRentCycleForPayment(rentCycle);

        validatePaymentAmount(
                request.getAmount(),
                rentCycle.getBalanceDue()
        );

        validatePaymentMethod(
                request.getPaymentMethod(),
                request.getTransactionReference()
        );

        validateTransactionReference(
                request.getTransactionReference()
        );

        Payment payment =
                paymentMapper.toEntity(request);

        payment.setRentCycle(rentCycle);

        /*
         * Manual Owner/Manager payment is already verified.
         */
        payment.setPaymentStatus(
                PaymentStatus.CONFIRMED
        );

        payment.setConfirmedBy(
                currentUser
        );

        payment.setConfirmedAt(
                LocalDateTime.now()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        /*
         * Update RentCycle.
         *
         * Partial payments are supported.
         */
        updateRentCycleAfterPayment(
                rentCycle,
                request.getAmount()
        );

        return paymentMapper.toResponse(
                savedPayment
        );
    }

    /*
     * =========================================================
     * SIMULATED TENANT PAYMENT
     * =========================================================
     *
     * This is our development/demo payment flow.
     *
     * Tenant clicks:
     *
     *      PAY ₹5000
     *
     * Backend:
     *
     *      1. Verifies tenant owns the rent cycle
     *      2. Validates amount
     *      3. Generates transaction reference
     *      4. Creates PENDING payment
     *
     * IMPORTANT:
     *
     * This does NOT update the RentCycle yet.
     *
     * Owner must confirm it.
     */
    public PaymentResponse simulatePayment(
            PaymentSimulationRequest request) {

        User currentUser = getCurrentUser();

        validateTenant(currentUser);

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
         * Make sure this rent cycle actually belongs
         * to the currently logged-in tenant.
         */
        validateTenantOwnsRentCycle(
                currentUser,
                rentCycle
        );

        validateRentCycleForPayment(rentCycle);

        /*
         * Tenant can make a partial payment.
         */
        validatePaymentAmount(
                request.getAmount(),
                rentCycle.getBalanceDue()
        );

        /*
         * Generate the transaction reference on the
         * backend instead of trusting the tenant to provide it.
         */
        String transactionReference =
                generateTransactionReference();

        Payment payment =
                new Payment();

        payment.setAmount(
                request.getAmount()
        );

        payment.setPaymentMethod(
                PaymentMethod.UPI
        );

        /*
         * Payment is NOT confirmed yet.
         */
        payment.setPaymentStatus(
                PaymentStatus.PENDING
        );

        payment.setTransactionReference(
                transactionReference
        );

        payment.setRentCycle(
                rentCycle
        );

        payment.setRemarks(
                "Simulated UPI payment - development mode"
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        /*
         * IMPORTANT:
         *
         * Do NOT update RentCycle here.
         *
         * It remains:
         *
         * PENDING
         * or
         * PARTIALLY_PAID
         *
         * until Owner confirms the payment.
         */

        return paymentMapper.toResponse(
                savedPayment
        );
    }

    /*
     * =========================================================
     * CONFIRM PAYMENT
     * =========================================================
     *
     * Owner/Manager confirms a PENDING payment.
     *
     * Only after this operation do we update the RentCycle.
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

        /*
         * Payment must still be pending.
         */
        if (payment.getPaymentStatus()
                == PaymentStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Payment is already confirmed"
            );
        }

        /*
         * We only allow confirmation of pending payments.
         */
        if (payment.getPaymentStatus()
                != PaymentStatus.PENDING) {

            throw new IllegalStateException(
                    "Only pending payments can be confirmed"
            );
        }

        /*
         * The transaction reference generated during
         * simulated payment must match.
         */
        if (!payment.getTransactionReference()
                .equals(
                        request.getTransactionReference()
                )) {

            throw new IllegalArgumentException(
                    "Transaction reference does not match"
            );
        }

        /*
         * Payment must still fit within the current
         * outstanding balance.
         */
        RentCycle rentCycle =
                payment.getRentCycle();

        if (payment.getAmount()
                .compareTo(
                        rentCycle.getBalanceDue()
                ) > 0) {

            throw new IllegalStateException(
                    "Payment amount exceeds the "
                            + "current outstanding balance"
            );
        }

        /*
         * Confirm payment.
         */
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
                paymentRepository.save(
                        payment
                );

        /*
         * NOW update RentCycle.
         *
         * This preserves partial payment logic.
         */
        updateRentCycleAfterPayment(
                rentCycle,
                payment.getAmount()
        );

        return paymentMapper.toResponse(
                confirmedPayment
        );
    }

    /*
     * =========================================================
     * GET ALL PAYMENTS
     * =========================================================
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
     * =========================================================
     * GET PAYMENT BY ID
     * =========================================================
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

        return paymentMapper.toResponse(
                payment
        );
    }

    /*
     * =========================================================
     * GET PAYMENTS BY RENT CYCLE
     * =========================================================
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByRentCycle(
            UUID rentCycleId) {

        if (!rentCycleRepository
                .findByIdAndActiveTrue(
                        rentCycleId
                )
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
     * =========================================================
     * UPDATE RENT CYCLE AFTER CONFIRMED PAYMENT
     * =========================================================
     *
     * This is where partial payment is handled.
     */
    private void updateRentCycleAfterPayment(
            RentCycle rentCycle,
            BigDecimal paymentAmount) {

        BigDecimal newAmountPaid =
                rentCycle.getAmountPaid()
                        .add(paymentAmount);

        BigDecimal newBalance =
                rentCycle.getAmountDue()
                        .subtract(newAmountPaid);

        /*
         * Prevent negative balance.
         */
        if (newBalance.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalStateException(
                    "Payment would make rent balance negative"
            );
        }

        rentCycle.setAmountPaid(
                newAmountPaid
        );

        rentCycle.setBalanceDue(
                newBalance
        );

        /*
         * Full payment.
         */
        if (newBalance.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            rentCycle.setStatus(
                    RentCycleStatus.PAID
            );

        }
        /*
         * Partial payment.
         */
        else {

            rentCycle.setStatus(
                    RentCycleStatus.PARTIALLY_PAID
            );
        }

        rentCycleRepository.save(
                rentCycle
        );
    }

    /*
     * =========================================================
     * VALIDATE RENT CYCLE
     * =========================================================
     */
    private void validateRentCycleForPayment(
            RentCycle rentCycle) {

        if (rentCycle.getStatus()
                == RentCycleStatus.PAID) {

            throw new IllegalStateException(
                    "Rent cycle is already fully paid"
            );
        }

        if (rentCycle.getBalanceDue()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalStateException(
                    "Rent cycle has no outstanding balance"
            );
        }
    }

    /*
     * =========================================================
     * VALIDATE PAYMENT AMOUNT
     * =========================================================
     *
     * Allows partial payment.
     */
    private void validatePaymentAmount(
            BigDecimal amount,
            BigDecimal balanceDue) {

        if (amount == null
                || amount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        if (amount.compareTo(
                balanceDue
        ) > 0) {

            throw new IllegalArgumentException(
                    "Payment amount cannot exceed "
                            + "the outstanding balance of "
                            + balanceDue
            );
        }
    }

    /*
     * =========================================================
     * VALIDATE PAYMENT METHOD
     * =========================================================
     */
    private void validatePaymentMethod(
            PaymentMethod paymentMethod,
            String transactionReference) {

        /*
         * UPI requires a transaction reference.
         */
        if (paymentMethod
                == PaymentMethod.UPI
                && (transactionReference == null
                || transactionReference.isBlank())) {

            throw new IllegalArgumentException(
                    "Transaction reference is required "
                            + "for UPI payment"
            );
        }

        /*
         * Cash should not contain a UPI reference.
         */
        if (paymentMethod
                == PaymentMethod.CASH
                && transactionReference != null
                && !transactionReference.isBlank()) {

            throw new IllegalArgumentException(
                    "Transaction reference should be empty "
                            + "for cash payment"
            );
        }
    }

    /*
     * =========================================================
     * TRANSACTION REFERENCE VALIDATION
     * =========================================================
     */
    private void validateTransactionReference(
            String transactionReference) {

        if (transactionReference != null
                && !transactionReference.isBlank()
                && paymentRepository
                .existsByTransactionReference(
                        transactionReference
                )) {

            throw new IllegalArgumentException(
                    "Transaction reference already exists"
            );
        }
    }

    /*
     * =========================================================
     * GENERATE SIMULATED TRANSACTION ID
     * =========================================================
     */
    private String generateTransactionReference() {

        String transactionReference;

        do {

            transactionReference =
                    "RF-UPI-"
                            + System.currentTimeMillis()
                            + "-"
                            + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();

        } while (
                paymentRepository
                        .existsByTransactionReference(
                                transactionReference
                        )
        );

        return transactionReference;
    }

    /*
     * =========================================================
     * VALIDATE CURRENT USER IS TENANT
     * =========================================================
     */
    private void validateTenant(
            User user) {

        if (user.getRole() != Role.TENANT) {

            throw new SecurityException(
                    "Only tenants can simulate a payment"
            );
        }
    }

    /*
     * =========================================================
     * VALIDATE TENANT OWNS RENT CYCLE
     * =========================================================
     *
     * Prevents James from paying another tenant's rent cycle.
     */
    private void validateTenantOwnsRentCycle(
            User user,
            RentCycle rentCycle) {

        if (rentCycle.getLease() == null
                || rentCycle.getLease().getTenant() == null
                || rentCycle.getLease()
                .getTenant()
                .getUser() == null) {

            throw new IllegalStateException(
                    "Rent cycle is not properly linked "
                            + "to a tenant"
            );
        }

        UUID tenantUserId =
                rentCycle.getLease()
                        .getTenant()
                        .getUser()
                        .getId();

        if (!tenantUserId.equals(
                user.getId()
        )) {

            throw new SecurityException(
                    "You are not authorized to pay "
                            + "this rent cycle"
            );
        }
    }

    /*
     * =========================================================
     * GET CURRENT USER
     * =========================================================
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
     * =========================================================
     * VALIDATE PAYMENT RECORDER
     * =========================================================
     *
     * Only OWNER and MANAGER can confirm payments
     * or manually record verified payments.
     */
    private void validatePaymentRecorder(
            User user) {

        if (user.getRole() != Role.OWNER
                && user.getRole() != Role.ADMIN) {

            throw new SecurityException(
                    "Only Owner or Property Manager "
                            + "can record or confirm payments"
            );
        }
    }
}