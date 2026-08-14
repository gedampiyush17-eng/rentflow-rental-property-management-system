package com.rentflow.lease.mapper;

import com.rentflow.lease.dto.LeaseCreateRequest;
import com.rentflow.lease.dto.LeaseResponse;
import com.rentflow.lease.dto.LeaseUpdateRequest;
import com.rentflow.lease.entity.Lease;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LeaseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "leaseStatus", ignore = true)
    Lease toEntity(LeaseCreateRequest request);

    @Mapping(source = "tenant.id", target = "tenantId")
    @Mapping(source = "unit.id", target = "unitId")
    LeaseResponse toResponse(Lease lease);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "leaseStatus", ignore = true)
    void updateEntity(
            LeaseUpdateRequest request,
            @MappingTarget Lease lease
    );
}