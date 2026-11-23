package cl.aldemar.trazamar.backend.controller;

import cl.aldemar.trazamar.backend.dto.LoginRequest;
import cl.aldemar.trazamar.backend.dto.LoginResponse;
import cl.aldemar.trazamar.backend.model.Usuario;
import cl.aldemar.trazamar.backend.security.AccesoService;
import cl.aldemar.trazamar.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login por rol")
public class AuthController {

    private final AuthService auth;
    private final AccesoService acceso;

    public AuthController(AuthService auth, AccesoService acceso) {
        this.auth = auth;
        this.acceso = acceso;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión: devuelve el token y el rol del usuario")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return auth.login(req);
    }

    @GetMapping("/me")
    @Operation(summary = "Ver quién soy (requiere token)")
    public Usuario yo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return acceso.usuarioActual(authorization);
    }
}
