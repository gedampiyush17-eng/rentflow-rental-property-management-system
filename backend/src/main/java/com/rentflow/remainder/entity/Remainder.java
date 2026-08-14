package com.rentflow.remainder.entity;

import com.rentflow.common.entity.BaseEntity;
import com.rentflow.remainder.enums.RemainderType;
import com.rentflow.rentcycle.entity.RentCycle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="remainder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Remainder extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(
            name = "remainder_type",
            nullable = false
    )
    private RemainderType remainderType;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private String recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "rent_cycle_id",
            nullable = false
    )
    private RentCycle rentCycle;
}
