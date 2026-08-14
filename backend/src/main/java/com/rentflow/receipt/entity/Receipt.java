package com.rentflow.receipt.entity;

import com.rentflow.common.entity.BaseEntity;
import com.rentflow.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "receipts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_receipt_payment",
                        columnNames = "payment_id"
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt extends BaseEntity {

    @Column(
            name = "receipt_number",
            nullable = false,
            unique = true
    )
    private String receiptNumber;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "payment_id",
            nullable = false,
            unique = true
    )
    private Payment payment;
}