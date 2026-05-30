package com.alora.auth.model.dto;

import com.alora.auth.model.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(
        @NotNull(message = "El rol es obligatorio")
        Role role
) {}
