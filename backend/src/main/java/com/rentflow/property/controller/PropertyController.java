package com.rentflow.property.controller;

import com.rentflow.property.dto.request.PropertyCreateRequest;
import com.rentflow.property.dto.request.PropertyUpdateRequest;
import com.rentflow.property.dto.response.PropertyResponse;
import com.rentflow.property.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name="Property", description="Property Management APIs")
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyService propertyService;

    @Operation(summary="Create a new Property")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse createProperty(@Valid @RequestBody PropertyCreateRequest request){
        return propertyService.createProperty(request);
    }

    @Operation(summary = "Get all active properties")
    @GetMapping
    public List<PropertyResponse> getAllProperties(){
        return propertyService.getAllProperties();
    }

    @Operation(summary = "Get property by ID")
    @GetMapping("/{id}")
    public PropertyResponse getPropertyById(@PathVariable UUID id){
        return propertyService.getPropertyById(id);
    }

    @Operation(summary = "Update property")
    @PutMapping("/{id}")
    public PropertyResponse updateProperty(
            @PathVariable UUID id,
            @Valid @RequestBody PropertyUpdateRequest request) {

        return propertyService.updateProperty(id, request);
    }

    @Operation(summary = "Soft delete property")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProperty(@PathVariable UUID id){
        propertyService.deleteProperty(id);
    }

}
