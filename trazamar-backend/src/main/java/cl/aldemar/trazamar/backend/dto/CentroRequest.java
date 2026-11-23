package cl.aldemar.trazamar.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CentroRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El código es obligatorio") String codigo
) {
}
