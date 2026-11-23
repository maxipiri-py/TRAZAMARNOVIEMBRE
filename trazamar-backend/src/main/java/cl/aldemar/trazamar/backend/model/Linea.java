package cl.aldemar.trazamar.backend.model;

import jakarta.persistence.*;

// Línea de cultivo (cuerda): pertenece a UN tren (trenId). Aquí se toman las muestras.
@Entity
@Table(name = "lineas")
public class Linea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Long trenId;

    private boolean activo = true;

    public Linea() { }

    public Linea(String nombre, Long trenId) {
        this.nombre = nombre;
        this.trenId = trenId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Long getTrenId() { return trenId; }
    public void setTrenId(Long trenId) { this.trenId = trenId; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
