package com.alora.api;

import com.alora.profile.dto.PrivateProfileDto;
import com.alora.profile.dto.PublicProfileDto;
import com.alora.profile.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final ProfileService service;

    public PublicController(ProfileService service) {
        this.service = service;
    }

    // GET /public/qr/{token} -> datos públicos
    @GetMapping("/qr/{token}")
    public ResponseEntity<PublicProfileDto> getByQr(@PathVariable String token) {
        PublicProfileDto dto = service.getPublicByQrToken(token);
        return ResponseEntity.ok(dto);
    }

    // POST /public/unlock -> valida PIN y devuelve privados
    public static record UnlockRequest(String token, String pin) {}

    @PostMapping("/unlock")
    public ResponseEntity<PrivateProfileDto> unlock(@RequestBody UnlockRequest req) {
        PrivateProfileDto dto = service.unlockByQrAndPin(req.token(), req.pin());
        return ResponseEntity.ok(dto);
    }
}
