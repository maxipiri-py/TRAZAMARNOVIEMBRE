package cl.aldemar.trazamar.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ENTIDAD = una tabla de la base de datos.
 * Cada objeto Muestra es una fila de la tabla "muestras".
 * Los nombres de los campos son los mismos que usa la app Android (MuestraDto),
 * así Retrofit puede convertir el JSON sin traducciones extra.
 */
@Entity
@Table(name = "muestras")
public class Muestra {

    // Llave primaria que genera la base de datos (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código único que genera la app (UUID). Sirve para no duplicar muestras al sincronizar offline.
    @Column(nullable = false, unique = true)
    private String codigo;

    // Dónde se tomó la muestra
    @Column(nullable = false)
    private Long centroId;
    @Column(nullable = false)
    private Long trenId;
    @Column(nullable = false)
    private Long lineaId;

    // Cuándo (texto: "2026-11-24" y "10:30", igual que en la app)
    @Column(nullable = false)
    private String fecha;
    private String hora;

    // Qué se midió
    private Double tramoMetros;
    @Column(nullable = false)
    private Long operadorId;
    private String operadorNombre;
    private Integer conteoIndividuos;
    private String observaciones;
    private Double calibrePromedio;
    private Double calibreMin;
    private Double calibreMax;
    private Double pesoMuestra;
    private String fotoUrl;

    // Estado de revisión: borrador -> pendiente -> observada -> corregida -> validada
    @Column(nullable = false)
    private String estado = "borrador";
    private String comentarioSupervisor;

    // ---------- Getters y setters ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Long getCentroId() { return centroId; }
    public void setCentroId(Long centroId) { this.centroId = centroId; }

    public Long getTrenId() { return trenId; }
    public void setTrenId(Long trenId) { this.trenId = trenId; }

    public Long getLineaId() { return lineaId; }
    public void setLineaId(Long lineaId) { this.lineaId = lineaId; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public Double getTramoMetros() { return tramoMetros; }
    public void setTramoMetros(Double tramoMetros) { this.tramoMetros = tramoMetros; }

    public Long getOperadorId() { return operadorId; }
    public void setOperadorId(Long operadorId) { this.operadorId = operadorId; }

    public String getOperadorNombre() { return operadorNombre; }
    public void setOperadorNombre(String operadorNombre) { this.operadorNombre = operadorNombre; }

    public Integer getConteoIndividuos() { return conteoIndividuos; }
    public void setConteoIndividuos(Integer conteoIndividuos) { this.conteoIndividuos = conteoIndividuos; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Double getCalibrePromedio() { return calibrePromedio; }
    public void setCalibrePromedio(Double calibrePromedio) { this.calibrePromedio = calibrePromedio; }

    public Double getCalibreMin() { return calibreMin; }
    public void setCalibreMin(Double calibreMin) { this.calibreMin = calibreMin; }

    public Double getCalibreMax() { return calibreMax; }
    public void setCalibreMax(Double calibreMax) { this.calibreMax = calibreMax; }

    public Double getPesoMuestra() { return pesoMuestra; }
    public void setPesoMuestra(Double pesoMuestra) { this.pesoMuestra = pesoMuestra; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getComentarioSupervisor() { return comentarioSupervisor; }
    public void setComentarioSupervisor(String comentarioSupervisor) { this.comentarioSupervisor = comentarioSupervisor; }
}
