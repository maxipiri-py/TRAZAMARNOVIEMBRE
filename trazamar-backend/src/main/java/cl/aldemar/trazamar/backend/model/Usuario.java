package cl.aldemar.trazamar.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Usuario del sistema. rol = operador | supervisor | analista | admin.
 * La contraseña NUNCA se guarda en texto: se guarda su hash SHA-256 (igual que en la app).
 * @JsonIgnore = estos campos nunca se envían en las respuestas JSON.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String rol;

    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    // Token de sesión que se entrega al hacer login (se reemplaza en cada login)
    @JsonIgnore
    private String token;

    private boolean activo = true;

    public Usuario() { }

    public Usuario(String username, String nombreCompleto, String rol, String passwordHash) {
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.passwordHash = passwordHash;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
