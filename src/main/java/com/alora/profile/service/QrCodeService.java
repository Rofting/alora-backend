package com.alora.profile.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value; // 👈 Asegúrate de importar esto
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class QrCodeService {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public byte[] generateQrCodeImage(String qrToken) throws Exception {
        // Usamos la variable inyectada
        String textToEncode = frontendUrl + qrToken;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                textToEncode,
                BarcodeFormat.QR_CODE,
                300,
                300
        );

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        return pngOutputStream.toByteArray();
    }
}