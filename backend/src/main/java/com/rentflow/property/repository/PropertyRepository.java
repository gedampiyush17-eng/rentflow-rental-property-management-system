package com.rentflow.property.repository;

import com.rentflow.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {

    List<Property> findByActiveTrue();

    Optional<Property> findByIdAndActiveTrue(UUID id);

    List<Property> findByOwnerIdAndActiveTrue(UUID ownerId);

    Optional<Property> findByIdAndOwnerIdAndActiveTrue(UUID id, UUID ownerId);
}