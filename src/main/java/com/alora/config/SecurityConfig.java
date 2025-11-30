package com.alora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 1. Indica que esta clase tiene configuración de Spring
@EnableWebSecurity // 2. Activa la seguridad web
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // Aquí definimos las reglas de quién pasa y quién no
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**" , "/care/**", "/auth/**").permitAll()

                        // Para cualquier otra cosa, exigimos que el usuario esté autenticado
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder(){
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}