package cl.aldemar.trazamar.backend.exception;

// Se lanza cuando se busca algo que no existe -> el backend responde 404
public class NoEncontradoException extends RuntimeException {
    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
