package com.alora.profile.controller;

import com.alora.profile.dto.PrivateProfileDto;
import com.alora.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles") // Ruta protegida (requiere Token)
@RequiredArgsConstructor
public class PrivateProfileController {

    private final ProfileService service;

    // Crear ficha
    @PostMapping
    public ResponseEntity<PrivateProfileDto> create(@RequestBody PrivateProfileDto dto) {
        PrivateProfileDto created = service.createProfile(dto);
        return ResponseEntity.ok(created);
    }

    // Listar mis perfiles
    @GetMapping
    public ResponseEntity<List<PrivateProfileDto>> getMyProfiles() {
        return ResponseEntity.ok(service.getMyProfiles());
    }

    //actualizar ficha
    @PutMapping("/{id}")
    public ResponseEntity<PrivateProfileDto> update(
            @PathVariable Long id,
            @RequestBody PrivateProfileDto dto
    ) {
        return ResponseEntity.ok(service.updateProfile(id, dto));
    }

    //borrar ficha
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

    //subir foto
    @PostMapping("/{id}/photo")
    public ResponseEntity<String> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file
    ) {
        String filename = service.uploadPhoto(id, file);
        return ResponseEntity.ok("Foto subida con éxito: " + filename);
    }
}