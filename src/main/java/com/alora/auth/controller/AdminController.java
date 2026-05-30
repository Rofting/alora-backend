package com.alora.auth.controller;

import com.alora.auth.model.dto.UpdateRoleRequest;
import com.alora.auth.model.dto.UserSummaryDto;
import com.alora.auth.repository.UserRepository;
import com.alora.exception.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryDto>> getAllUsers() {
        return ResponseEntity.ok(toSummaryList());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserSummaryDto> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(toSummary(u)))
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserSummaryDto> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        user.setRole(request.role());
        return ResponseEntity.ok(toSummary(userRepository.save(user)));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── helpers ──────────────────────────────────────────
    private List<UserSummaryDto> toSummaryList() {
        return userRepository.findAll().stream().map(this::toSummary).toList();
    }

    private UserSummaryDto toSummary(com.alora.auth.model.User u) {
        return new UserSummaryDto(u.getId(), u.getEmail(), u.getFullName(), u.getRole().name());
    }
}