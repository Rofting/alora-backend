package com.alora.profile.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class QrCodeService {

    private final String FRONTEND_URL = "http://192.168.1.196:5173/?token=";

    public byte[] generateQrCodeImage(String qrToken) throws Exception {
        // 1. Unimos la URL con el token único del paciente
        String textToEncode = FRONTEND_URL + qrToken;

        // 2. Configuramos el escritor de QR
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // 3. Generamos la matriz (ancho y alto de 300px)
        BitMatrix bitMatrix = qrCodeWriter.encode(
                textToEncode,
                BarcodeFormat.QR_CODE,
                300,
                300
        );

        // 4. Convertimos la matriz en bytes (formato PNG)
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        return pngOutputStream.toByteArray();
    }
}