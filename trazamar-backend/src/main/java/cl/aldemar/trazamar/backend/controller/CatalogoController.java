package cl.aldemar.trazamar.backend.controller;

import cl.aldemar.trazamar.backend.dto.CentroRequest;
import cl.aldemar.trazamar.backend.dto.LineaRequest;
import cl.aldemar.trazamar.backend.dto.TrenRequest;
import cl.aldemar.trazamar.backend.model.Centro;
import cl.aldemar.trazamar.backend.model.Linea;
import cl.aldemar.trazamar.backend.model.Tren;
import cl.aldemar.trazamar.backend.security.AccesoService;
import cl.aldemar.trazamar.backend.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@Tag(name = "Catálogos", description = "Centros, trenes y líneas de cultivo")
public class CatalogoController {

    private final CatalogoService service;
    private final AccesoService acceso;

    public CatalogoController(CatalogoService service, AccesoService acceso) {
        this.service = service;
        this.acceso = acceso;
    }

    // ---------- Leer: cualquiera (la app los necesita para llenar los selectores) ----------

    @GetMapping("/centros")
    @Operation(summary = "Listar centros activos")
    public List<Centro> centros() {
        return service.listarCentros();
    }

    @GetMapping("/trenes")
    @Operation(summary = "Listar trenes (opcional: de un centro)")
    public List<Tren> trenes(@RequestParam(required = false) Long centroId) {
        return service.listarTrenes(centroId);
    }

    @GetMapping("/lineas")
    @Operation(summary = "Listar líneas (opcional: de un tren)")
    public List<Linea> lineas(@RequestParam(required = false) Long trenId) {
        return service.listarLineas(trenId);
    }

    // ---------- Crear: solo admin ----------

    @PostMapping("/centros")
    @Operation(summary = "Crear centro (solo admin)")
    public ResponseEntity<Centro> crearCentro(@RequestHeader(value = "Authorization", required = false) String authorization,
                                              @Valid @RequestBody CentroRequest req) {
        acceso.exigirRol(authorization, "admin");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCentro(req));
    }

    @PostMapping("/trenes")
    @Operation(summary = "Crear tren (solo admin)")
    public ResponseEntity<Tren> crearTren(@RequestHeader(value = "Authorization", required = false) String authorization,
                                          @Valid @RequestBody TrenRequest req) {
        acceso.exigirRol(authorization, "admin");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearTren(req));
    }

    @PostMapping("/lineas")
    @Operation(summary = "Crear línea (solo admin)")
    public ResponseEntity<Linea> crearLinea(@RequestHeader(value = "Authorization", required = false) String authorization,
                                            @Valid @RequestBody LineaRequest req) {
        acceso.exigirRol(authorization, "admin");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearLinea(req));
    }
}
