package com.alora.auth.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El email no puede estar vacio")
        @Email
        String email,

        @NotBlank(message = "La contraseña no puede estar vacia")
        String password
) {}
