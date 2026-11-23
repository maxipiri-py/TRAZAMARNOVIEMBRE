package cl.aldemar.trazamar.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Lo que envía el supervisor al revisar una muestra.
 * estado: "validada" u "observada". Si es "observada", el comentario es obligatorio.
 */
public record RevisionRequest(
        @NotBlank(message = "El estado es obligatorio") String estado,
        String comentario
) {
}
