package com.rentflow.receipt.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.rentflow.common.exception.ResourceNotFoundException;
import com.rentflow.receipt.entity.Receipt;
import com.rentflow.receipt.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptPdfService {

    private final ReceiptRepository receiptRepository;

    public byte[] generateReceiptPdf(UUID receiptId) {

        Receipt receipt =
                receiptRepository
                        .findByIdAndActiveTrue(receiptId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Receipt not found with id: "
                                                + receiptId
                                ));

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        Document document = new Document();

        try {

            PdfWriter.getInstance(
                    document,
                    outputStream
            );

            document.open();

            document.add(
                    new Paragraph("RENTFLOW")
            );

            document.add(
                    new Paragraph(
                            "Payment Receipt"
                    )
            );

            document.add(
                    new Paragraph(
                            "Receipt Number: "
                                    + receipt.getReceiptNumber()
                    )
            );

            document.add(
                    new Paragraph(
                            "Issued At: "
                                    + receipt.getIssuedAt()
                    )
            );

            document.add(
                    new Paragraph(
                            "Payment ID: "
                                    + receipt
                                    .getPayment()
                                    .getId()
                    )
            );

            document.add(
                    new Paragraph(
                            "Amount: ₹"
                                    + receipt
                                    .getPayment()
                                    .getAmount()
                    )
            );

            document.add(
                    new Paragraph(
                            "Payment Method: "
                                    + receipt
                                    .getPayment()
                                    .getPaymentMethod()
                    )
            );

            document.add(
                    new Paragraph(
                            "Transaction Reference: "
                                    + receipt
                                    .getPayment()
                                    .getTransactionReference()
                    )
            );

            document.add(
                    new Paragraph(
                            "Rent Cycle: "
                                    + receipt
                                    .getPayment()
                                    .getRentCycle()
                                    .getId()
                    )
            );

            document.add(
                    new Paragraph(
                            "Period: "
                                    + receipt
                                    .getPayment()
                                    .getRentCycle()
                                    .getPeriodStart()
                                    + " to "
                                    + receipt
                                    .getPayment()
                                    .getRentCycle()
                                    .getPeriodEnd()
                    )
            );

            document.add(
                    new Paragraph(
                            "Thank you for your payment."
                    )
            );

            document.close();

            return outputStream.toByteArray();

        } catch (DocumentException e) {

            throw new IllegalStateException(
                    "Failed to generate receipt PDF",
                    e
            );

        } finally {

            try {
                outputStream.close();
            } catch (IOException ignored) {
            }
        }
    }
}