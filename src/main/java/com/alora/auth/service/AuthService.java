package com.alora.auth.service;

import com.alora.auth.model.dto.LoginRequest;
import com.alora.auth.model.dto.LoginResponse;
import com.alora.auth.model.dto.RegisterRequest;
import com.alora.auth.model.Role;
import com.alora.auth.model.User;
import com.alora.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        User user = User.builder()
                .email(request.email())
                .fullName(request.fullName())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .createdAt(java.time.Instant.now())
                .build();

        userRepository.save(user);
        return new LoginResponse(jwtService.generateToken(user));
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new LoginResponse(jwtService.generateToken(user));
    }
}
