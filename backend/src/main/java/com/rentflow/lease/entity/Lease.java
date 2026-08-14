package com.rentflow.lease.entity;

import com.rentflow.common.entity.BaseEntity;
import com.rentflow.lease.enums.LeaseStatus;
import com.rentflow.tenant.entity.Tenant;
import com.rentflow.unit.entity.Unit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "leases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lease extends BaseEntity {

    @Column(nullable = false)
    private LocalDate leaseStartDate;

    @Column(nullable = false)
    private LocalDate leaseEndDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyRent;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal securityDeposit;

    @Column(nullable = false)
    private Integer paymentDueDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaseStatus leaseStatus;

    /*
     * A tenant can have multiple leases over time.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /*
     * A unit can have multiple historical leases,
     * but only one ACTIVE lease at a time.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;
}