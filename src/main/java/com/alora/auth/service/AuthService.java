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

    // --- REGISTRO ---
    public LoginResponse register(RegisterRequest request) {

        // 1. Crear el usuario
        User user = User.builder()
                .email(request.email())
                .fullName(request.fullName())

                .password( passwordEncoder.encode(request.password()) )
                .role(Role.USER)
                .createdAt(java.time.Instant.now())
                .build();

        // 2: Manda el usuario a la base de datos
        userRepository.save(user);

        // 3. Generar token
        String token = jwtService.generateToken(user);
        return new LoginResponse(token);
    }

    // --- LOGIN ---
    public LoginResponse login(LoginRequest request) {

        // 1. AUTENTICACIÓN AUTOMÁTICA
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // 2. Si pasamos la línea de arriba, es que el usuario es válido.
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. GENERAR EL TOKEN

        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }
}