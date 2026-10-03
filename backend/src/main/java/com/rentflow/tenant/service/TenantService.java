package com.rentflow.tenant.service;

import com.rentflow.auth.entity.User;
import com.rentflow.auth.enums.Role;
import com.rentflow.auth.repository.UserRepository;
import com.rentflow.common.exception.ResourceNotFoundException;
import com.rentflow.tenant.dto.TenantCreateRequest;
import com.rentflow.tenant.dto.request.TenantUpdateRequest;
import com.rentflow.tenant.dto.response.TenantResponse;
import com.rentflow.tenant.entity.Tenant;
import com.rentflow.tenant.mapper.TenantMapper;
import com.rentflow.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TenantResponse createTenant(
            TenantCreateRequest request) {

        if (tenantRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        if (tenantRepository.existsByPhoneNumber(
                request.getPhoneNumber())) {

            throw new IllegalArgumentException(
                    "Phone number already exists"
            );
        }

        if (request.getAadhaarNumber() != null
                && tenantRepository.existsByAadhaarNumber(
                request.getAadhaarNumber())) {

            throw new IllegalArgumentException(
                    "Aadhaar number already exists"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "User email already exists"
            );
        }

        if (userRepository.existsByPhoneNumber(
                request.getPhoneNumber())) {

            throw new IllegalArgumentException(
                    "User phone number already exists"
            );
        }

        // Create the authentication account
        User user = new User();

        user.setFullName(
                request.getFirstName() + " "
                        + request.getLastName()
        );

        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setPhoneNumber(request.getPhoneNumber());

        user.setRole(Role.TENANT);

        User savedUser =
                userRepository.save(user);

        // Create the Tenant profile
        Tenant tenant =
                tenantMapper.toEntity(request);

        tenant.setUser(savedUser);

        Tenant savedTenant =
                tenantRepository.save(tenant);

        return tenantMapper.toResponse(savedTenant);
    }

    public List<TenantResponse> getAllTenants() {

        return tenantRepository.findByActiveTrue()
                .stream()
                .map(tenantMapper::toResponse)
                .toList();
    }

    public TenantResponse getTenantById(UUID id) {

        Tenant tenant =
                tenantRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant not found with id: "
                                                + id
                                ));

        return tenantMapper.toResponse(tenant);
    }

    public TenantResponse updateTenant(
            UUID id,
            TenantUpdateRequest request) {

        Tenant tenant =
                tenantRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant not found with id: "
                                                + id
                                ));

        if (!tenant.getEmail()
                .equals(request.getEmail())
                && tenantRepository.existsByEmail(
                request.getEmail())) {

            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        if (!tenant.getPhoneNumber()
                .equals(request.getPhoneNumber())
                && tenantRepository.existsByPhoneNumber(
                request.getPhoneNumber())) {

            throw new IllegalArgumentException(
                    "Phone number already exists"
            );
        }

        if (request.getAadhaarNumber() != null
                && !request.getAadhaarNumber()
                .equals(tenant.getAadhaarNumber())
                && tenantRepository.existsByAadhaarNumber(
                request.getAadhaarNumber())) {

            throw new IllegalArgumentException(
                    "Aadhaar number already exists"
            );
        }

        tenantMapper.updateEntity(request, tenant);

        Tenant updatedTenant =
                tenantRepository.save(tenant);

        return tenantMapper.toResponse(updatedTenant);
    }

    public void deleteTenant(UUID id) {

        Tenant tenant =
                tenantRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tenant not found with id: "
                                                + id
                                ));

        tenant.setActive(false);

        tenantRepository.save(tenant);
    }
}