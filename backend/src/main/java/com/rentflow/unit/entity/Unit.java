package com.rentflow.unit.entity;

import com.rentflow.common.entity.BaseEntity;
import com.rentflow.property.entity.Property;
import com.rentflow.unit.enums.OccupancyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "units")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit extends BaseEntity {

    @Column(name = "unit_number", nullable = false)
    private String unitNumber;

    @Column(precision = 12, scale = 2)
    private BigDecimal monthlyRent;

    @Column(precision = 12, scale = 2)
    private BigDecimal securityDeposit;

    @Column(precision = 10, scale = 2)
    private BigDecimal area;

    @Enumerated(EnumType.STRING)
    @Column(name = "occupancy_status", nullable = false)
    private OccupancyStatus occupancyStatus;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
}