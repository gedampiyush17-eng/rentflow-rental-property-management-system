package com.rentflow.property.repository;

import com.rentflow.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
}
