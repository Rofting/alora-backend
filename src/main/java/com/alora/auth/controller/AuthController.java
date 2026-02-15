package com.alora.auth.controller;

import com.alora.auth.model.dto.LoginRequest;
import com.alora.auth.model.dto.LoginResponse;
import com.alora.auth.model.dto.RegisterRequest;
import com.alora.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // --- ENDPOINT DE REGISTRO ---
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
            @RequestBody RegisterRequest request
    ) {
        // 6. Llama al método de registro del servicio
        return ResponseEntity.ok(authService.register(request));
    }

    // --- ENDPOINT DE LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ){
        // 9. Llama al método de login del servicio
        return ResponseEntity.ok(authService.login(request));
    }
}