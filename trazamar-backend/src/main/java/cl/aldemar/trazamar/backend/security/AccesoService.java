package cl.aldemar.trazamar.backend.security;

import cl.aldemar.trazamar.backend.exception.NoAutorizadoException;
import cl.aldemar.trazamar.backend.exception.ProhibidoException;
import cl.aldemar.trazamar.backend.model.Usuario;
import cl.aldemar.trazamar.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;

/**
 * "Guardia de seguridad" del backend.
 * 1) Calcula el hash SHA-256 de las contraseñas (mismo formato que hashPassword() de la app).
 * 2) Revisa el token que llega en el encabezado "Authorization: Bearer <token>".
 * 3) Revisa si el ROL del usuario tiene permiso para la acción.
 */
@Service
public class AccesoService {

    private final UsuarioRepository usuarios;

    public AccesoService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    // Busca al usuario dueño del token. Si no hay token o no es válido -> 401
    public Usuario usuarioActual(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new NoAutorizadoException("Debes iniciar sesión (falta el token)");
        }
        String token = authorization.substring(7).trim();
        Usuario u = usuarios.findByToken(token)
                .orElseThrow(() -> new NoAutorizadoException("Sesión inválida o vencida, inicia sesión de nuevo"));
        if (!u.isActivo()) {
            throw new NoAutorizadoException("La cuenta está desactivada");
        }
        return u;
    }

    // Exige que el usuario tenga uno de los roles indicados. Si no -> 403
    public Usuario exigirRol(String authorization, String... roles) {
        Usuario u = usuarioActual(authorization);
        boolean permitido = Arrays.stream(roles).anyMatch(r -> r.equalsIgnoreCase(u.getRol()));
        if (!permitido) {
            throw new ProhibidoException("Tu rol (" + u.getRol() + ") no tiene permiso para esta acción. Roles permitidos: " + String.join(", ", roles));
        }
        return u;
    }
}
