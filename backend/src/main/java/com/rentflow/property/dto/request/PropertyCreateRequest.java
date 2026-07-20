package com.rentflow.property.dto.request;

import com.rentflow.property.enums.PropertyStatus;
import com.rentflow.property.enums.PropertyType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PropertyCreateRequest {
    @NotBlank
    private String propertyName;

    @NotNull
    private PropertyType propertyType;

    private String description;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;

    @NotBlank
    private String pincode;

    @PositiveOrZero
    private Integer totalUnits;

    @PositiveOrZero
    private Integer occupiedUnits;

    @NotNull
    private PropertyStatus status;

}
