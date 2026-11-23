package cl.aldemar.trazamar.backend.dto;

import jakarta.validation.constraints.NotBlank;

// Lo que envía la app al iniciar sesión
public record LoginRequest(
        @NotBlank(message = "El usuario es obligatorio") String username,
        @NotBlank(message = "La contraseña es obligatoria") String password
) {
}
