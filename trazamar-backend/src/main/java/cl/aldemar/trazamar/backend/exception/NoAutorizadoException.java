package cl.aldemar.trazamar.backend.exception;

// Login incorrecto o falta el token -> 401
public class NoAutorizadoException extends RuntimeException {
    public NoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}
