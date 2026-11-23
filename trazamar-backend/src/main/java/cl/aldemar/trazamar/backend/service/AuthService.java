package cl.aldemar.trazamar.backend.service;

import cl.aldemar.trazamar.backend.dto.LoginRequest;
import cl.aldemar.trazamar.backend.dto.LoginResponse;
import cl.aldemar.trazamar.backend.exception.NoAutorizadoException;
import cl.aldemar.trazamar.backend.model.Usuario;
import cl.aldemar.trazamar.backend.repository.UsuarioRepository;
import cl.aldemar.trazamar.backend.security.AccesoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;

    public AuthService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    // Revisa usuario + contraseña. Si está bien, devuelve su token (lo crea si no tenía) y su rol.
    // Se reutiliza el token para que el mismo usuario pueda estar en el teléfono y en Swagger a la vez.
    @Transactional
    public LoginResponse login(LoginRequest req) {
        Usuario u = usuarios.findByUsernameIgnoreCase(req.username().trim())
                .orElseThrow(() -> new NoAutorizadoException("Usuario o contraseña incorrectos"));
        if (!u.isActivo() || !u.getPasswordHash().equals(AccesoService.hash(req.password()))) {
            throw new NoAutorizadoException("Usuario o contraseña incorrectos, o cuenta desactivada");
        }
        if (u.getToken() == null) {
            u.setToken(UUID.randomUUID().toString());
            usuarios.save(u);
        }
        return new LoginResponse(u.getToken(), u);
    }
}
