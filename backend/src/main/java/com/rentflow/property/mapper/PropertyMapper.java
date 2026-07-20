package com.rentflow.property.mapper;

import com.rentflow.property.dto.request.PropertyCreateRequest;
import com.rentflow.property.dto.response.PropertyResponse;
import com.rentflow.property.entity.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface PropertyMapper {

    @Mapping(target="id", ignore=true)
    @Mapping(target="createdAt", ignore=true)
    @Mapping(target="updatedAt", ignore=true)
    @Mapping(target="active",ignore=true)
    @Mapping(target="occupiedUnits",ignore=true)
    Property toEntity(PropertyCreateRequest request);

    PropertyResponse toResponse(Property property);
}
