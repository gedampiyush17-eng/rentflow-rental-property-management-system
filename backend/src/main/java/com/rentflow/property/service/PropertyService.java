package com.rentflow.property.service;

import com.rentflow.property.dto.request.PropertyCreateRequest;
import com.rentflow.property.dto.response.PropertyResponse;
import com.rentflow.property.entity.Property;
import com.rentflow.property.mapper.PropertyMapper;
import com.rentflow.property.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    public PropertyResponse createProperty(PropertyCreateRequest request){
        // Convert Request DTO-> Entity
        Property property= propertyMapper.toEntity(request);

        // Save into database
        Property savedProperty = propertyRepository.save(property);

        // Convert Entity ->Response DTO
        return propertyMapper.toResponse(savedProperty);
    }
}
