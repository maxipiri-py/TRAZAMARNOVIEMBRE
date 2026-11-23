package cl.aldemar.trazamar.backend.service;

import cl.aldemar.trazamar.backend.dto.UsuarioRequest;
import cl.aldemar.trazamar.backend.exception.NoEncontradoException;
import cl.aldemar.trazamar.backend.exception.ReglaNegocioException;
import cl.aldemar.trazamar.backend.model.Usuario;
import cl.aldemar.trazamar.backend.repository.UsuarioRepository;
import cl.aldemar.trazamar.backend.security.AccesoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UsuarioService {

    public static final Set<String> ROLES = Set.of("operador", "supervisor", "analista", "admin");

    private final UsuarioRepository usuarios;

    public UsuarioService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public List<Usuario> listar() {
        return usuarios.findAllByOrderByNombreCompleto();
    }

    public Usuario crear(UsuarioRequest req) {
        String rol = req.rol().trim().toLowerCase();
        if (!ROLES.contains(rol)) {
            throw new ReglaNegocioException("Rol no válido. Usa: " + String.join(", ", ROLES));
        }
        if (usuarios.findByUsernameIgnoreCase(req.username().trim()).isPresent()) {
            throw new ReglaNegocioException("Ese nombre de usuario ya existe");
        }
        return usuarios.save(new Usuario(req.username().trim(), req.nombreCompleto().trim(), rol,
                AccesoService.hash(req.password())));
    }

    public Usuario cambiarActivo(Long id, boolean activo) {
        Usuario u = usuarios.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el usuario " + id));
        u.setActivo(activo);
        if (!activo) u.setToken(null); // al desactivar, se cierra su sesión
        return usuarios.save(u);
    }
}
