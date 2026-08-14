package com.rentflow.remainder.controller;

import com.rentflow.remainder.service.RemainderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/remainders")
@RequiredArgsConstructor
@Tag(
        name = "Remainder",
        description = "Rent payment remainder APIs"
)
public class RemainderController {

    private final RemainderService remainderService;

    @Operation(
            summary = "Process rent payment remainders"
    )
    @PostMapping("/process")
    public ResponseEntity<String> processRemainders() {

        remainderService.processRemainders();

        return ResponseEntity.ok(
                "Remainders processed successfully"
        );
    }
}