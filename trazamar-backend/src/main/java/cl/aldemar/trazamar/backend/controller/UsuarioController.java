package cl.aldemar.trazamar.backend.controller;

import cl.aldemar.trazamar.backend.dto.UsuarioRequest;
import cl.aldemar.trazamar.backend.model.Usuario;
import cl.aldemar.trazamar.backend.security.AccesoService;
import cl.aldemar.trazamar.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Administración de cuentas y roles (solo admin)")
public class UsuarioController {

    private final UsuarioService service;
    private final AccesoService acceso;

    public UsuarioController(UsuarioService service, AccesoService acceso) {
        this.service = service;
        this.acceso = acceso;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios (solo admin)")
    public List<Usuario> listar(@RequestHeader(value = "Authorization", required = false) String authorization) {
        acceso.exigirRol(authorization, "admin");
        return service.listar();
    }

    @PostMapping
    @Operation(summary = "Crear usuario con su rol (solo admin)")
    public ResponseEntity<Usuario> crear(@RequestHeader(value = "Authorization", required = false) String authorization,
                                         @Valid @RequestBody UsuarioRequest req) {
        acceso.exigirRol(authorization, "admin");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(req));
    }

    @PatchMapping("/{id}/activo")
    @Operation(summary = "Activar o desactivar un usuario (solo admin)")
    public Usuario cambiarActivo(@RequestHeader(value = "Authorization", required = false) String authorization,
                                 @PathVariable Long id, @RequestParam boolean activo) {
        acceso.exigirRol(authorization, "admin");
        return service.cambiarActivo(id, activo);
    }
}
