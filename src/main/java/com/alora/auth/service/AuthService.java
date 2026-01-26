package com.alora.auth.service;

import com.alora.auth.model.User;
import com.alora.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método para registrar un nuevo cuidador/familiar
    public User register(String email, String rawPassword, String fullName) {
        // 1. Verificar si ya existe
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // 2. Crear el usuario con la contraseña ENCRIPTADA
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .fullName(fullName)
                .build();

        // 3. Guardar en BBDD
        return userRepository.save(user);
    }

    public User authenticate(String email, String rawPassword) {
        // 1. Buscamos al usuario. Si no existe, lanzamos error.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Comparamos la contraseña que entra con el HASH de la BBDD
        if (!passwordEncoder.matches(rawPassword,user.getPassword())){
            throw new RuntimeException("Contraseña incorrecta");
        }

        return user;
    }
}