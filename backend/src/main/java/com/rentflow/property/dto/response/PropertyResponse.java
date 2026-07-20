package com.rentflow.property.dto.response;

import com.rentflow.property.enums.PropertyStatus;
import com.rentflow.property.enums.PropertyType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PropertyResponse {

    private UUID id;

    private String propertyName;

    private PropertyType propertyType;

    private String description;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String pincode;

    private Integer totalUnits;

    private Integer occupiedUnits;

    private PropertyStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
