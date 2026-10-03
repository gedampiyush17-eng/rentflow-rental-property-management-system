package com.rentflow.receipt.mapper;

import com.rentflow.receipt.dto.ReceiptResponse;
import com.rentflow.receipt.entity.Receipt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface ReceiptMapper {

    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "amount", source = "payment.amount")
    @Mapping(target = "paymentMethod", source = "payment.paymentMethod")
    @Mapping(target = "paymentDate", source = "payment.confirmedAt")
    @Mapping(target = "transactionReference",
            source = "payment.transactionReference")

    @Mapping(target = "rentCycleId", source = "payment.rentCycle.id")
    @Mapping(target = "periodStart",
            source = "payment.rentCycle.periodStart")
    @Mapping(target = "periodEnd",
            source = "payment.rentCycle.periodEnd")

    @Mapping(target = "tenantId",
            source = "payment.rentCycle.lease.tenant.id")

    @Mapping(target = "unitId",
            source = "payment.rentCycle.lease.unit.id")

    @Mapping(target = "propertyId",
            source = "payment.rentCycle.lease.unit.property.id")
    ReceiptResponse toResponse(Receipt receipt);
}
