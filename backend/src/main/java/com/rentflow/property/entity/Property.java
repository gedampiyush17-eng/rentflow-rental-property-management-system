package com.rentflow.property.entity;

import com.rentflow.common.entity.BaseEntity;
import com.rentflow.property.enums.PropertyStatus;
import com.rentflow.property.enums.PropertyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name="properties")
@Data
public class Property extends BaseEntity {
    @NotBlank
    @Column(name="property_name", nullable=false)
    private String propertyName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="property_type",nullable=false)
    private PropertyType propertyType;

    @Column(length=500)
    private String description;

    @NotBlank
    @Column(name="address_line_1",nullable=false)
    private String addressLine1;

    @Column(name="address_line_2")
    private String addressLine2;

    @NotBlank
    @Column(nullable=false)
    private String city;

    @NotBlank
    @Column(nullable = false)
    private String state;

    @NotBlank
    @Column(nullable = false)
    private String country;

    @NotBlank
    @Column(nullable = false)
    private String pincode;

    @PositiveOrZero
    @Column(name = "total_units")
    private Integer totalUnits;

    @PositiveOrZero
    @Column(name = "occupied_units")
    private Integer occupiedUnits;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status;

}
