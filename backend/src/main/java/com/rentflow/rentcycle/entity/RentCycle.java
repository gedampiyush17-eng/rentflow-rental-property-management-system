package com.rentflow.rentcycle.entity;

import com.rentflow.common.entity.BaseEntity;
import com.rentflow.lease.entity.Lease;
import com.rentflow.rentcycle.enums.RentCycleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="rent_cycles",
        uniqueConstraints = {
            @UniqueConstraint(
                    name="uk_rent_cycle_lease_period",
                    columnNames={
                            "lease_id",
                            "period_start",
                            "period_end"
                    }
            )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentCycle extends BaseEntity {

    @Column(name="period_start",nullable=false)
    private LocalDate periodStart;

    @Column(name="period_end",nullable=false)
    private LocalDate periodEnd;

    @Column(name="due_date",nullable=false)
    private LocalDate dueDate;

    @Column(name="amount_due",nullable=false,precision = 12,scale=2)
    private BigDecimal amountDue;

    @Column(name="amount_paid",nullable = false,precision = 12,scale=2)
    private BigDecimal amountPaid=BigDecimal.ZERO;

    @Column(name="balance_due",nullable=false,precision=12,scale=2)
    private BigDecimal balanceDue;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private RentCycleStatus status;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="lease_id", nullable=false)
    private Lease lease;
}
