package com.rentflow.unit.mapper;

import com.rentflow.unit.dto.request.UnitCreateRequest;
import com.rentflow.unit.dto.request.UnitUpdateRequest;
import com.rentflow.unit.dto.response.UnitResponse;
import com.rentflow.unit.entity.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UnitMapper {

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        @Mapping(target = "active", ignore = true)
        @Mapping(target = "property", ignore = true)
        @Mapping(target = "occupancyStatus", ignore = true)
        Unit toEntity(UnitCreateRequest request);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        @Mapping(target = "active", ignore = true)
        @Mapping(target = "property", ignore = true)
        @Mapping(target = "occupancyStatus", ignore = true)
        void updateEntity(
                UnitUpdateRequest request,
                @MappingTarget Unit unit
        );

        @Mapping(
                target = "propertyId",
                source = "property.id"
        )
        UnitResponse toResponse(Unit unit);
}