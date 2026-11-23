package cl.aldemar.trazamar.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LineaRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotNull(message = "El tren es obligatorio") Long trenId
) {
}
