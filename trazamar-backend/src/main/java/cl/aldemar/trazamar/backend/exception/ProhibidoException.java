package cl.aldemar.trazamar.backend.exception;

// El usuario inició sesión, pero su ROL no tiene permiso para esa acción -> 403
public class ProhibidoException extends RuntimeException {
    public ProhibidoException(String mensaje) {
        super(mensaje);
    }
}
