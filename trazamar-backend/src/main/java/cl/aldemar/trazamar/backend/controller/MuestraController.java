package cl.aldemar.trazamar.backend.controller;

import cl.aldemar.trazamar.backend.dto.MuestraRequest;
import cl.aldemar.trazamar.backend.dto.RevisionRequest;
import cl.aldemar.trazamar.backend.model.Muestra;
import cl.aldemar.trazamar.backend.security.AccesoService;
import cl.aldemar.trazamar.backend.service.MuestraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLLER = la "puerta de entrada". Define las URLs (endpoints) que la app llama con Retrofit.
 * Base: http://localhost:8080/api/muestras
 */
@RestController
@RequestMapping("/api/muestras")
@Tag(name = "Muestras", description = "Registro y revisión de muestras de choritos")
public class MuestraController {

    private final MuestraService service;
    private final AccesoService acceso;

    public MuestraController(MuestraService service, AccesoService acceso) {
        this.service = service;
        this.acceso = acceso;
    }

    @GetMapping
    @Operation(summary = "Listar muestras (filtros opcionales por estado, centro y línea)")
    public List<Muestra> listar(@RequestParam(required = false) String estado,
                                @RequestParam(required = false) Long centroId,
                                @RequestParam(required = false) Long lineaId) {
        return service.listar(estado, centroId, lineaId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una muestra por su id")
    public Muestra obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear una muestra (queda en estado borrador)")
    public ResponseEntity<Muestra> crear(@Valid @RequestBody MuestraRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar una muestra (solo borrador u observada)")
    public Muestra actualizar(@PathVariable Long id, @Valid @RequestBody MuestraRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una muestra (solo borrador; rol operador o admin)")
    public ResponseEntity<Void> eliminar(@RequestHeader(value = "Authorization", required = false) String authorization,
                                         @PathVariable Long id) {
        acceso.exigirRol(authorization, "operador", "admin");
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enviar")
    @Operation(summary = "Operador envía la muestra a revisión (rol operador o admin)")
    public Muestra enviar(@RequestHeader(value = "Authorization", required = false) String authorization,
                          @PathVariable Long id) {
        acceso.exigirRol(authorization, "operador", "admin");
        return service.enviarARevision(id);
    }

    @PatchMapping("/{id}/revision")
    @Operation(summary = "Supervisor valida u observa la muestra (rol supervisor o admin)")
    public Muestra revisar(@RequestHeader(value = "Authorization", required = false) String authorization,
                           @PathVariable Long id, @Valid @RequestBody RevisionRequest req) {
        acceso.exigirRol(authorization, "supervisor", "admin");
        return service.revisar(id, req);
    }
}
