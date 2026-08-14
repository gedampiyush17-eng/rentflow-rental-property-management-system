package com.rentflow.qr.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.rentflow.common.exception.ResourceNotFoundException;
import com.rentflow.rentcycle.entity.RentCycle;
import com.rentflow.rentcycle.repository.RentCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrService {

    private final RentCycleRepository rentCycleRepository;

    public String generateQrCode(UUID rentCycleId){

        RentCycle rentCycle=rentCycleRepository.findByIdAndActiveTrue(rentCycleId)
                .orElseThrow(()->new ResourceNotFoundException("Rent Cycle not found with Id: "+rentCycleId));

        String upiUri= buildUpiUri(rentCycle);

        try{
            BitMatrix bitMatrix=new MultiFormatWriter().encode(upiUri, BarcodeFormat.QR_CODE,300,300);

            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(bitMatrix,"PNG",outputStream);

            byte[] qrBytes=outputStream.toByteArray();

            return Base64.getEncoder().encodeToString(qrBytes);
        }catch(WriterException | IOException e){
            throw new IllegalStateException("Failed to generate QR Code",e);
        }

    }

    private String buildUpiUri(RentCycle rentCycle){
        String upiId="rentflow@upi";

        String payeeName="RentFlow";

        String amount=rentCycle.getAmountDue().toPlainString();

        String transactionNote="Rent-"+rentCycle.getId();

        return "upi://pay"
                + "?pa=" + upiId
                + "&pn=" + payeeName
                + "&am=" + amount
                + "&cu=INR"
                + "&tn=" + transactionNote;
    }
}
