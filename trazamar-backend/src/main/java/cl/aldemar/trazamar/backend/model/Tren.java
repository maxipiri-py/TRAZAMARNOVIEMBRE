package cl.aldemar.trazamar.backend.model;

import jakarta.persistence.*;

// Tren de cultivo: pertenece a UN centro (centroId) y tiene varias líneas.
@Entity
@Table(name = "trenes")
public class Tren {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Long centroId;

    private boolean activo = true;

    public Tren() { }

    public Tren(String nombre, Long centroId) {
        this.nombre = nombre;
        this.centroId = centroId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Long getCentroId() { return centroId; }
    public void setCentroId(Long centroId) { this.centroId = centroId; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
