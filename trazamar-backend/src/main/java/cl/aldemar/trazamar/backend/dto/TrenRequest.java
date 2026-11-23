package cl.aldemar.trazamar.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrenRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotNull(message = "El centro es obligatorio") Long centroId
) {
}
