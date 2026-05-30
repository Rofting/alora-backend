package com.alora.profile.controller;

import com.alora.profile.dto.PrivateProfileDto;
import com.alora.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class PrivateProfileController {

    private final ProfileService service;

    @GetMapping("/{id}")
    public ResponseEntity<PrivateProfileDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProfileById(id));
    }

    @PostMapping
    public ResponseEntity<PrivateProfileDto> create(@Valid @RequestBody PrivateProfileDto dto) {
        return ResponseEntity.ok(service.createProfile(dto));
    }

    @GetMapping
    public ResponseEntity<List<PrivateProfileDto>> getMyProfiles() {
        return ResponseEntity.ok(service.getMyProfiles());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrivateProfileDto> update(
            @PathVariable Long id,
            @RequestBody PrivateProfileDto dto
    ) {
        return ResponseEntity.ok(service.updateProfile(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<String> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file
    ) {
        return ResponseEntity.ok(service.uploadPhoto(id, file));
    }
}
