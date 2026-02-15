package com.alora.profile.controller;

import com.alora.profile.dto.PrivateProfileDto;
import com.alora.profile.dto.PublicProfileDto;
import com.alora.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public/profile") // La ruta base para escaneos
@RequiredArgsConstructor
public class PublicProfileController {

    private final ProfileService service;

    // --- ENDPOINT 1: Escanear QR (Público) ---
    // URL: GET /public/profile/{qrToken}
    @GetMapping("/{qrToken}")
    public ResponseEntity<PublicProfileDto> getProfile(
            @PathVariable String qrToken
    ) {
        // 2. Llama al método 'getPublicByQrToken' del servicio
        return ResponseEntity.ok(service.getPublicByQrToken(qrToken));
    }

    // --- ENDPOINT 2: Meter el PIN (Desbloqueo) ---
    // URL: POST /public/profile/{qrToken}/unlock
    @PostMapping("/{qrToken}/unlock")
    public ResponseEntity<PrivateProfileDto> unlockProfile(
            @PathVariable String qrToken,
            @RequestBody Map<String, String> request
    ) {
        String pin = request.get("pin");

        // 3. Llama al método 'unlockByQrAndPin' del servicio pasándole token y pin
        return ResponseEntity.ok(service.unlockByQrAndPin(qrToken, pin));
    }
}