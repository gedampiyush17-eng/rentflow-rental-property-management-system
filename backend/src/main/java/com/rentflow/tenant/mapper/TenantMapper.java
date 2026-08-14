package com.rentflow.tenant.mapper;

import com.rentflow.tenant.dto.request.TenantCreateRequest;
import com.rentflow.tenant.dto.request.TenantUpdateRequest;
import com.rentflow.tenant.dto.response.TenantResponse;
import com.rentflow.tenant.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "user", ignore = true)
    Tenant toEntity(TenantCreateRequest request);

    @Mapping(target = "userId", source = "user.id")
    TenantResponse toResponse(Tenant tenant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntity(
            TenantUpdateRequest request,
            @MappingTarget Tenant tenant
    );
}