package cl.aldemar.trazamar.backend.service;

import cl.aldemar.trazamar.backend.dto.CentroRequest;
import cl.aldemar.trazamar.backend.dto.LineaRequest;
import cl.aldemar.trazamar.backend.dto.TrenRequest;
import cl.aldemar.trazamar.backend.exception.NoEncontradoException;
import cl.aldemar.trazamar.backend.exception.ReglaNegocioException;
import cl.aldemar.trazamar.backend.model.Centro;
import cl.aldemar.trazamar.backend.model.Linea;
import cl.aldemar.trazamar.backend.model.Tren;
import cl.aldemar.trazamar.backend.repository.CentroRepository;
import cl.aldemar.trazamar.backend.repository.LineaRepository;
import cl.aldemar.trazamar.backend.repository.TrenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Catálogos: la jerarquía del cultivo  Centro -> Tren -> Línea.
 * También valida que una muestra apunte a un centro/tren/línea que existen y que "calzan" entre sí.
 */
@Service
public class CatalogoService {

    private final CentroRepository centros;
    private final TrenRepository trenes;
    private final LineaRepository lineas;

    public CatalogoService(CentroRepository centros, TrenRepository trenes, LineaRepository lineas) {
        this.centros = centros;
        this.trenes = trenes;
        this.lineas = lineas;
    }

    public List<Centro> listarCentros() {
        return centros.findByActivoTrueOrderByNombre();
    }

    public List<Tren> listarTrenes(Long centroId) {
        return centroId == null ? trenes.findByActivoTrueOrderByNombre()
                : trenes.findByCentroIdAndActivoTrueOrderByNombre(centroId);
    }

    public List<Linea> listarLineas(Long trenId) {
        return trenId == null ? lineas.findByActivoTrueOrderByNombre()
                : lineas.findByTrenIdAndActivoTrueOrderByNombre(trenId);
    }

    public Centro crearCentro(CentroRequest req) {
        if (centros.existsByCodigoIgnoreCase(req.codigo().trim())) {
            throw new ReglaNegocioException("Ya existe un centro con el código " + req.codigo());
        }
        return centros.save(new Centro(req.nombre().trim(), req.codigo().trim()));
    }

    public Tren crearTren(TrenRequest req) {
        centros.findById(req.centroId())
                .orElseThrow(() -> new NoEncontradoException("No existe el centro " + req.centroId()));
        return trenes.save(new Tren(req.nombre().trim(), req.centroId()));
    }

    public Linea crearLinea(LineaRequest req) {
        trenes.findById(req.trenId())
                .orElseThrow(() -> new NoEncontradoException("No existe el tren " + req.trenId()));
        return lineas.save(new Linea(req.nombre().trim(), req.trenId()));
    }

    // Regla: la línea debe pertenecer al tren, y el tren al centro
    public void validarJerarquia(Long centroId, Long trenId, Long lineaId) {
        Tren tren = trenes.findById(trenId)
                .orElseThrow(() -> new ReglaNegocioException("El tren " + trenId + " no existe"));
        Linea linea = lineas.findById(lineaId)
                .orElseThrow(() -> new ReglaNegocioException("La línea " + lineaId + " no existe"));
        if (!tren.getCentroId().equals(centroId)) {
            throw new ReglaNegocioException("El tren " + trenId + " no pertenece al centro " + centroId);
        }
        if (!linea.getTrenId().equals(trenId)) {
            throw new ReglaNegocioException("La línea " + lineaId + " no pertenece al tren " + trenId);
        }
    }
}
