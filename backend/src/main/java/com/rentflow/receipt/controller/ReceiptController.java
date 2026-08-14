package com.rentflow.receipt.controller;

import com.rentflow.receipt.dto.ReceiptResponse;
import com.rentflow.receipt.service.ReceiptPdfService;
import com.rentflow.receipt.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@Tag(
        name = "Receipt",
        description = "Payment Receipt Management APIs"
)
public class ReceiptController {

    private final ReceiptService receiptService;
    private final ReceiptPdfService receiptPdfService;

    @Operation(
            summary = "Generate receipt for a confirmed payment"
    )
    @PostMapping("/payment/{paymentId}")
    public ReceiptResponse generateReceipt(
            @PathVariable UUID paymentId) {

        return receiptService.generateReceipt(paymentId);
    }

    @Operation(
            summary = "Get receipt by ID"
    )
    @GetMapping("/{id}")
    public ReceiptResponse getReceiptById(
            @PathVariable UUID id) {

        return receiptService.getReceiptById(id);
    }

    @Operation(
            summary = "Get receipt by payment ID"
    )
    @GetMapping("/payment/{paymentId}")
    public ReceiptResponse getReceiptByPayment(
            @PathVariable UUID paymentId) {

        return receiptService.getReceiptByPayment(paymentId);
    }

    @Operation(
            summary = "Generate receipt PDF"
    )
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateReceiptPdf(
            @PathVariable UUID id) {

        byte[] pdf =
                receiptPdfService.generateReceiptPdf(id);

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=receipt-" + id + ".pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}