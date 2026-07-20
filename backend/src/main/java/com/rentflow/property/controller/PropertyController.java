package com.rentflow.property.controller;

import com.rentflow.property.dto.request.PropertyCreateRequest;
import com.rentflow.property.dto.response.PropertyResponse;
import com.rentflow.property.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyService propertyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse createProperty(@Valid @RequestBody PropertyCreateRequest request){
        return propertyService.createProperty(request);
    }
}
