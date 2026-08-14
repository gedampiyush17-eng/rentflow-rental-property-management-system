package com.rentflow.tenant.repository;

import com.rentflow.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository
        extends JpaRepository<Tenant, UUID> {

    List<Tenant> findByActiveTrue();

    Optional<Tenant> findByIdAndActiveTrue(UUID id);

    Optional<Tenant> findByUserId(UUID userId);

    Optional<Tenant> findByUserIdAndActiveTrue(UUID userId);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByAadhaarNumber(String aadhaarNumber);

    boolean existsByUserId(UUID userId);
}