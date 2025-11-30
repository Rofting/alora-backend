package com.alora.api;

import com.alora.auth.dto.RegisterRequest;
import com.alora.auth.model.User;
import com.alora.auth.service.AuthService;
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

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User newUser = authService.register(
                request.email(),
                request.password(),
                request.fullName()
        );

        // Devolvemos un 201 Created
        return ResponseEntity
                .created(URI.create("/user/" + newUser.getId()))
                .body(newUser);
    }
}
