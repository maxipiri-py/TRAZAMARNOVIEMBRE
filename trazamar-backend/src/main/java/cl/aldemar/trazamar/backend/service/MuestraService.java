package cl.aldemar.trazamar.backend.service;

import cl.aldemar.trazamar.backend.dto.MuestraRequest;
import cl.aldemar.trazamar.backend.dto.RevisionRequest;
import cl.aldemar.trazamar.backend.exception.NoEncontradoException;
import cl.aldemar.trazamar.backend.exception.ReglaNegocioException;
import cl.aldemar.trazamar.backend.model.Muestra;
import cl.aldemar.trazamar.backend.repository.MuestraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * SERVICE = el "cerebro". Aquí viven las reglas del negocio de ALDEMAR:
 * qué estados existen, quién puede editar, cuándo se puede enviar a revisión, etc.
 * El controlador solo recibe la petición y le pregunta al servicio.
 */
@Service
public class MuestraService {

    private final MuestraRepository repo;
    private final CatalogoService catalogos;

    // Spring "inyecta" el repositorio y el servicio de catálogos automáticamente (inyección por constructor)
    public MuestraService(MuestraRepository repo, CatalogoService catalogos) {
        this.repo = repo;
        this.catalogos = catalogos;
    }

    // ---------- LEER ----------

    // Lista todas las muestras; los filtros son opcionales (si vienen null, no filtran)
    public List<Muestra> listar(String estado, Long centroId, Long lineaId) {
        return repo.findAll().stream()
                .filter(m -> estado == null || estado.isBlank() || m.getEstado().equalsIgnoreCase(estado))
                .filter(m -> centroId == null || m.getCentroId().equals(centroId))
                .filter(m -> lineaId == null || m.getLineaId().equals(lineaId))
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .toList();
    }

    public Muestra obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe la muestra con id " + id));
    }

    // ---------- CREAR ----------

    @Transactional
    public Muestra crear(MuestraRequest req) {
        // Si la app reenvía una muestra que ya llegó (sincronización offline), devolvemos la existente:
        // así NUNCA se duplica una muestra.
        if (req.codigo() != null && !req.codigo().isBlank()) {
            var existente = repo.findByCodigo(req.codigo());
            if (existente.isPresent()) {
                return existente.get();
            }
        }
        // Regla: centro, tren y línea deben existir y pertenecer uno al otro
        catalogos.validarJerarquia(req.centroId(), req.trenId(), req.lineaId());
        Muestra m = new Muestra();
        m.setCodigo(req.codigo() == null || req.codigo().isBlank() ? UUID.randomUUID().toString() : req.codigo());
        copiarDatos(req, m);
        m.setEstado("borrador");
        return repo.save(m);
    }

    // ---------- ACTUALIZAR ----------

    @Transactional
    public Muestra actualizar(Long id, MuestraRequest req) {
        Muestra m = obtener(id);
        // Regla: solo se edita una muestra en borrador u observada
        if (!m.getEstado().equals("borrador") && !m.getEstado().equals("observada")) {
            throw new ReglaNegocioException("Solo se pueden editar muestras en borrador u observadas (estado actual: " + m.getEstado() + ")");
        }
        catalogos.validarJerarquia(req.centroId(), req.trenId(), req.lineaId());
        copiarDatos(req, m);
        return repo.save(m);
    }

    // ---------- ELIMINAR ----------

    @Transactional
    public void eliminar(Long id) {
        Muestra m = obtener(id);
        if (!m.getEstado().equals("borrador")) {
            throw new ReglaNegocioException("Solo se pueden eliminar muestras en borrador");
        }
        repo.delete(m);
    }

    // ---------- CAMBIOS DE ESTADO ----------

    // El operador envía la muestra al supervisor
    @Transactional
    public Muestra enviarARevision(Long id) {
        Muestra m = obtener(id);
        if (!m.getEstado().equals("borrador") && !m.getEstado().equals("observada")) {
            throw new ReglaNegocioException("Solo se envían muestras en borrador u observadas");
        }
        if (m.getConteoIndividuos() == null || m.getConteoIndividuos() <= 0) {
            throw new ReglaNegocioException("El conteo debe ser mayor a 0 para enviar a revisión");
        }
        // Misma regla que Reglas.estadoAlEnviar() de la app: observada -> corregida; si no -> pendiente
        m.setEstado(m.getEstado().equals("observada") ? "corregida" : "pendiente");
        return repo.save(m);
    }

    // El supervisor valida u observa la muestra
    @Transactional
    public Muestra revisar(Long id, RevisionRequest req) {
        Muestra m = obtener(id);
        if (!m.getEstado().equals("pendiente") && !m.getEstado().equals("corregida")) {
            throw new ReglaNegocioException("Solo se revisan muestras pendientes o corregidas");
        }
        String nuevo = req.estado().toLowerCase();
        if (!nuevo.equals("validada") && !nuevo.equals("observada")) {
            throw new ReglaNegocioException("El estado de revisión debe ser 'validada' u 'observada'");
        }
        // Misma regla que Validador.comentarioSupervisorValido() de la app
        if (nuevo.equals("observada") && (req.comentario() == null || req.comentario().trim().length() < 5)) {
            throw new ReglaNegocioException("Para observar una muestra el comentario debe tener al menos 5 caracteres");
        }
        m.setEstado(nuevo);
        m.setComentarioSupervisor(req.comentario());
        return repo.save(m);
    }

    // Copia los campos del formulario a la entidad (se usa al crear y al editar)
    private void copiarDatos(MuestraRequest req, Muestra m) {
        m.setCentroId(req.centroId());
        m.setTrenId(req.trenId());
        m.setLineaId(req.lineaId());
        m.setFecha(req.fecha());
        m.setHora(req.hora());
        m.setTramoMetros(req.tramoMetros());
        m.setOperadorId(req.operadorId());
        m.setOperadorNombre(req.operadorNombre());
        m.setConteoIndividuos(req.conteoIndividuos());
        m.setObservaciones(req.observaciones());
        m.setCalibrePromedio(req.calibrePromedio());
        m.setCalibreMin(req.calibreMin());
        m.setCalibreMax(req.calibreMax());
        m.setPesoMuestra(req.pesoMuestra());
        m.setFotoUrl(req.fotoUrl());
    }
}
