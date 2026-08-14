package com.rentflow.payment.entity;

import com.rentflow.auth.entity.User;
import com.rentflow.common.entity.BaseEntity;
import com.rentflow.lease.entity.Lease;
import com.rentflow.payment.enums.PaymentMethod;
import com.rentflow.payment.enums.PaymentStatus;
import com.rentflow.rentcycle.entity.RentCycle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Column(nullable = false,precision = 12,scale=2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(unique = true)
    private String transactionReference;

    private LocalDateTime confirmedAt;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="confirmed_by")
    private User confirmedBy;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="rent_cycle_id",nullable=false)
    private RentCycle rentCycle;

    @Column(length=500)
    private String remarks;
}
