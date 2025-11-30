package com.alora.auth.repository;

import com.alora.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data crea la consulta SQL automáticamente al leer este nombre
    Optional<User> findByEmail(String email);

    // También nos servirá para verificar si un email ya existe al registrarse
    boolean existsByEmail(String email);
}