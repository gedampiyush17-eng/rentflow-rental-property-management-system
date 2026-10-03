package com.rentflow.payment.mapper;

import com.rentflow.payment.dto.PaymentCreateRequest;
import com.rentflow.payment.dto.PaymentResponse;
import com.rentflow.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)

    @Mapping(target = "rentCycle", ignore = true)
    @Mapping(target = "confirmedBy", ignore = true)
    @Mapping(target = "confirmedAt", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)

    Payment toEntity(PaymentCreateRequest request);


    @Mapping(target = "rentCycleId", source = "rentCycle.id")
    @Mapping(target = "confirmedBy", source = "confirmedBy.id")
    PaymentResponse toResponse(Payment payment);
}