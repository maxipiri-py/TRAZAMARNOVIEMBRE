package cl.aldemar.trazamar.backend.exception;

// Se lanza cuando se rompe una regla del negocio (ej: editar una muestra ya validada) -> responde 409
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
