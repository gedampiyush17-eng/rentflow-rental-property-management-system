package com.rentflow.auth.mapper;

import com.rentflow.auth.dto.RegisterRequest;
import com.rentflow.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target="id",ignore=true)
    @Mapping(target="createdAt",ignore=true)
    @Mapping(target="updatedAt",ignore=true)
    @Mapping(target="active",ignore=true)
    User toEntity(RegisterRequest request);

}
