package cl.aldemar.trazamar.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO = "formulario" que llega desde la app (o desde Swagger) para crear o editar una muestra.
 * Las anotaciones validan los datos ANTES de guardarlos. Si algo falla, el backend responde 400.
 */
public record MuestraRequest(
        // Si la app no envía código, el backend genera uno
        String codigo,

        @NotNull(message = "El centro es obligatorio") Long centroId,
        @NotNull(message = "El tren es obligatorio") Long trenId,
        @NotNull(message = "La línea es obligatoria") Long lineaId,

        @NotBlank(message = "La fecha es obligatoria") String fecha,
        String hora,

        @NotNull(message = "El tramo es obligatorio")
        @Positive(message = "El tramo debe ser mayor a 0") Double tramoMetros,

        @NotNull(message = "El operador es obligatorio") Long operadorId,
        String operadorNombre,

        @PositiveOrZero(message = "El conteo no puede ser negativo") Integer conteoIndividuos,
        String observaciones,
        @PositiveOrZero Double calibrePromedio,
        @PositiveOrZero Double calibreMin,
        @PositiveOrZero Double calibreMax,
        @PositiveOrZero(message = "El peso no puede ser negativo") Double pesoMuestra,
        String fotoUrl
) {
}
