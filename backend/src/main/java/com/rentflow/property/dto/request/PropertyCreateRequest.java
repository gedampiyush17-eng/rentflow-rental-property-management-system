package com.rentflow.property.dto.request;

import com.rentflow.property.enums.PropertyStatus;
import com.rentflow.property.enums.PropertyType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PropertyCreateRequest {
    @NotBlank(message = "Property name is required")
    private String propertyName;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    private String description;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    @Min(value = 1, message = "Total units must be at least 1")
    private Integer totalUnits;

    @PositiveOrZero
    private Integer occupiedUnits;

    @NotNull
    private PropertyStatus status;

}
