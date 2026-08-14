package com.rentflow.tenant.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TenantResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String occupation;

    private String aadhaarNumber;

    private LocalDate dateOfBirth;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private UUID userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}