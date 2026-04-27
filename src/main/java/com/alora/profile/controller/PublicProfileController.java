package com.alora.profile.controller;

import com.alora.profile.dto.PrivateProfileDto;
import com.alora.profile.dto.PublicProfileDto;
import com.alora.profile.service.ProfileService;
import com.alora.profile.service.QrCodeService; // 👈 1. Asegúrate de importar esto
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType; // 👈 2. Import para MediaType
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicProfileController {

    private final ProfileService service;
    private final QrCodeService qrCodeService;

    // --- ENDPOINT 1: Escanear QR (Público) ---
    @GetMapping("/{qrToken}")
    public ResponseEntity<PublicProfileDto> getProfile(
            @PathVariable String qrToken
    ) {
        return ResponseEntity.ok(service.getPublicByQrToken(qrToken));
    }

    // --- ENDPOINT 2: Meter el PIN (Desbloqueo) ---
    @PostMapping("/{qrToken}/unlock")
    public ResponseEntity<PrivateProfileDto> unlockProfile(
            @PathVariable String qrToken,
            @RequestBody Map<String, String> request
    ) {
        String pin = request.get("pin");
        return ResponseEntity.ok(service.unlockByQrAndPin(qrToken, pin));
    }

    // --- ENDPOINT 3: Generar Imagen QR (Nuevo) ---
    @GetMapping("/{qrToken}/qr-image")
    public ResponseEntity<byte[]> getQrImage(@PathVariable String qrToken) throws Exception {
        // Generamos la imagen usando el token que viene en la URL
        byte[] image = qrCodeService.generateQrCodeImage(qrToken);

        // Devolvemos la imagen como PNG para que el navegador la muestre directamente
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }
}