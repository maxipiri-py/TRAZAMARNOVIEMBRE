package cl.aldemar.trazamar.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Datos para que el administrador cree una cuenta nueva
public record UsuarioRequest(
        @NotBlank(message = "El usuario es obligatorio") String username,
        @NotBlank @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres") String password,
        @NotBlank(message = "El nombre es obligatorio") String nombreCompleto,
        @NotBlank(message = "El rol es obligatorio") String rol
) {
}
