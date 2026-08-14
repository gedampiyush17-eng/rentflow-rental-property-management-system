package com.rentflow.property.service;

import com.rentflow.common.exception.ResourceNotFoundException;
import com.rentflow.property.dto.request.PropertyCreateRequest;
import com.rentflow.property.dto.request.PropertyUpdateRequest;
import com.rentflow.property.dto.response.PropertyResponse;
import com.rentflow.property.entity.Property;
import com.rentflow.property.enums.PropertyStatus;
import com.rentflow.property.mapper.PropertyMapper;
import com.rentflow.property.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    public PropertyResponse createProperty(
            PropertyCreateRequest request) {

        Property property = propertyMapper.toEntity(request);

        property.setStatus(PropertyStatus.ACTIVE);

        /*
         * Owner will be assigned from the authenticated user.
         * We will add this after fixing the Auth/User module.
         */

        Property savedProperty =
                propertyRepository.save(property);

        return propertyMapper.toResponse(savedProperty);
    }

    public PropertyResponse updateProperty(
            UUID id,
            PropertyUpdateRequest request) {

        Property property =
                propertyRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        propertyMapper.updateEntity(request, property);

        Property updatedProperty =
                propertyRepository.save(property);

        return propertyMapper.toResponse(updatedProperty);
    }

    public void deleteProperty(UUID id) {

        Property property =
                propertyRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found with id: " + id
                                ));

        property.setActive(false);

        propertyRepository.save(property);
    }

    public List<PropertyResponse> getAllProperties() {

        return propertyRepository.findByActiveTrue()
                .stream()
                .map(propertyMapper::toResponse)
                .toList();
    }

    public PropertyResponse getPropertyById(UUID id) {

        Property property =
                propertyRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found with id: " + id
                                ));

        return propertyMapper.toResponse(property);
    }
}