package com.alora.auth.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails { // <-- 1. AHORA ES UN "PASAPORTE" DE SPRING

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private Instant createdAt;

    // --- NUEVO CAMPO PARA SEGURIDAD ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    // =====================================================================
    // MÉTODOS OBLIGATORIOS DE LA INTERFAZ UserDetails
    // =====================================================================

    // 1. ¿Qué permisos tiene? Convierte nuestro Role en un Authority de Spring
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    // 2. ¿Con qué se loguea? Con el email.
    @Override
    public String getUsername() {
        return email;
    }

    // (El getPassword() ya lo tienes creado gracias al @Getter de Lombok)

    // 3. ¿La cuenta está activa? (De momento, ponemos que todo está OK)
    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta no ha expirado
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta no está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // La contraseña no ha caducado
    }

    @Override
    public boolean isEnabled() {
        return true; // El usuario está habilitado
    }
}