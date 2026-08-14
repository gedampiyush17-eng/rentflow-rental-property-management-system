package com.rentflow.rentcycle.mapper;

import com.rentflow.rentcycle.entity.RentCycle;
import com.rentflow.rentcycle.dto.RentCycleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RentCycleMapper {

    @Mapping(target="leaseId",source = "lease.id")
    @Mapping(target="tenantId",source="lease.tenant.id")
    @Mapping(target="unitId",source="lease.unit.id")
    RentCycleResponse toResponse(RentCycle rentCycle);
}
