package com.alora.profile.controller;

import com.alora.profile.dto.PrivateProfileDto;
import com.alora.profile.dto.PublicProfileDto;
import com.alora.profile.service.ProfileService;
import com.alora.profile.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @GetMapping("/{qrToken}")
    public ResponseEntity<PublicProfileDto> getProfile(@PathVariable String qrToken) {
        return ResponseEntity.ok(service.getPublicByQrToken(qrToken));
    }

    @PostMapping("/{qrToken}/unlock")
    public ResponseEntity<PrivateProfileDto> unlockProfile(
            @PathVariable String qrToken,
            @RequestBody Map<String, String> request
    ) {
        return ResponseEntity.ok(service.unlockByQrAndPin(qrToken, request.get("pin")));
    }

    @GetMapping("/{qrToken}/qr-image")
    public ResponseEntity<byte[]> getQrImage(@PathVariable String qrToken) throws Exception {
        byte[] image = qrCodeService.generateQrCodeImage(qrToken);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }
}
