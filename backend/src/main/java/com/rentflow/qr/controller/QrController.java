package com.rentflow.qr.controller;

import com.rentflow.qr.service.QrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@Tag(name="Qr Code", description = "UPI QR Code Generation APIs")
public class QrController {

    private final QrService qrService;

    @Operation(summary="Generate UPI QR code for a rent cycle")
    @GetMapping("/{rentCycleId}")
    public String generateQrCode(@PathVariable UUID rentCycleId){

        return qrService.generateQrCode(rentCycleId);
    }
}
