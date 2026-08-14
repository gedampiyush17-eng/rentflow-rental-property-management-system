package com.rentflow.property.mapper;

import com.rentflow.property.dto.request.PropertyCreateRequest;
import com.rentflow.property.dto.request.PropertyUpdateRequest;
import com.rentflow.property.dto.response.PropertyResponse;
import com.rentflow.property.entity.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "units", ignore = true)
    @Mapping(target = "status", ignore = true)
    Property toEntity(PropertyCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "units", ignore = true)
    void updateEntity(
            PropertyUpdateRequest request,
            @MappingTarget Property property
    );

    PropertyResponse toResponse(Property property);
}