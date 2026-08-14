package com.rentflow.rentcycle.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RentCycleGenerateRequest {

    @NotNull(message="lease is required")
    private UUID leaseId;
}
