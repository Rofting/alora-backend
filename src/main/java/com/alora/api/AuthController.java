package com.alora.api;

import com.alora.auth.dto.LoginRequest;
import com.alora.auth.dto.LoginResponse;
import com.alora.auth.dto.RegisterRequest;
import com.alora.auth.model.User;
import com.alora.auth.service.AuthService;
import com.alora.auth.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")

public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {

        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User newUser = authService.register(
                request.email(),
                request.password(),
                request.fullName()
        );

        return ResponseEntity
                .created(URI.create("/user/" + newUser.getId()))
                .body(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User authenticatedUser = authService.authenticate(
                request.email(),
                request.password());

        //Generamos el token para ese usuario
        String token = jwtService.generateToken(authenticatedUser.getEmail());

        // Devolvemos el token
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
