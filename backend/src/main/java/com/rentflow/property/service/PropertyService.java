package com.rentflow.property.service;

import com.rentflow.common.exception.ResourceNotFoundException;
import com.rentflow.property.dto.request.PropertyCreateRequest;
import com.rentflow.property.dto.request.PropertyUpdateRequest;
import com.rentflow.property.dto.response.PropertyResponse;
import com.rentflow.property.entity.Property;
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

    public PropertyResponse createProperty(PropertyCreateRequest request){
        // Convert Request DTO-> Entity
        Property property= propertyMapper.toEntity(request);

        // Save into database
        Property savedProperty = propertyRepository.save(property);

        // Convert Entity ->Response DTO
        return propertyMapper.toResponse(savedProperty);
    }

    public PropertyResponse updateProperty(UUID id, PropertyUpdateRequest request){

        Property property=propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not Found"));

        property.setPropertyName(request.getPropertyName());
        property.setPropertyName(request.getPropertyName());
        property.setDescription(request.getDescription());
        property.setAddressLine1(request.getAddressLine1());
        property.setAddressLine2(request.getAddressLine2());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setCountry(request.getCountry());
        property.setPincode(request.getPincode());
        property.setTotalUnits(request.getTotalUnits());
        property.setStatus(request.getStatus());

        Property updatedProperty=propertyRepository.save(property);

        return propertyMapper.toResponse(updatedProperty);

    }

    public void deleteProperty(UUID id){
        Property property= propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        property.setActive(false);
        propertyRepository.save(property);
    }

    public List<PropertyResponse> getAllProperties(){
        return propertyRepository.findByActiveTrue()
                .stream()
                .map(propertyMapper::toResponse)
                .toList();
    }

    public PropertyResponse getPropertyById(UUID id){
        Property property = propertyRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Property not found with id: " + id));

        return propertyMapper.toResponse(property);
    }


}
