package cl.aldemar.trazamar.backend.config;

import cl.aldemar.trazamar.backend.model.Centro;
import cl.aldemar.trazamar.backend.model.Linea;
import cl.aldemar.trazamar.backend.model.Muestra;
import cl.aldemar.trazamar.backend.model.Tren;
import cl.aldemar.trazamar.backend.model.Usuario;
import cl.aldemar.trazamar.backend.repository.CentroRepository;
import cl.aldemar.trazamar.backend.repository.LineaRepository;
import cl.aldemar.trazamar.backend.repository.MuestraRepository;
import cl.aldemar.trazamar.backend.repository.TrenRepository;
import cl.aldemar.trazamar.backend.repository.UsuarioRepository;
import cl.aldemar.trazamar.backend.security.AccesoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Se ejecuta una vez al iniciar el backend.
 * Si las tablas están vacías, inserta datos FICTICIOS: usuarios demo, catálogo y 3 muestras.
 * Usuarios demo (contraseña 1234, iguales a los de la app): OP-01, OP-02, SUP-01, AN-01, ADMIN-01.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final MuestraRepository muestras;
    private final CentroRepository centros;
    private final TrenRepository trenes;
    private final LineaRepository lineas;
    private final UsuarioRepository usuarios;

    public DatosIniciales(MuestraRepository muestras, CentroRepository centros, TrenRepository trenes,
                          LineaRepository lineas, UsuarioRepository usuarios) {
        this.muestras = muestras;
        this.centros = centros;
        this.trenes = trenes;
        this.lineas = lineas;
        this.usuarios = usuarios;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() == 0) {
            String clave = AccesoService.hash("1234");
            usuarios.save(new Usuario("OP-01", "Operador Uno", "operador", clave));
            usuarios.save(new Usuario("OP-02", "Operador Dos", "operador", clave));
            usuarios.save(new Usuario("SUP-01", "Supervisor Uno", "supervisor", clave));
            usuarios.save(new Usuario("AN-01", "Analista Uno", "analista", clave));
            usuarios.save(new Usuario("ADMIN-01", "Administrador Uno", "admin", clave));
        }

        if (centros.count() == 0) {
            Centro melon = centros.save(new Centro("Centro El Melón", "CM-01"));
            Centro ancud = centros.save(new Centro("Centro Ancud Norte", "AN-02"));
            Tren tren1 = trenes.save(new Tren("Tren 1", melon.getId()));
            Tren trenA = trenes.save(new Tren("Tren A", ancud.getId()));
            lineas.save(new Linea("Línea 1", tren1.getId()));
            lineas.save(new Linea("Línea 2", tren1.getId()));
            lineas.save(new Linea("Línea A1", trenA.getId()));
        }

        if (muestras.count() == 0) {
            muestras.save(muestra(1L, 1L, 1L, "2026-11-10", "09:15", 0.2, 120, 45.0, "pendiente", "Muestra inicial línea 1"));
            muestras.save(muestra(1L, 1L, 1L, "2026-11-17", "09:40", 0.2, 138, 51.5, "validada", "Segunda medición línea 1"));
            muestras.save(muestra(1L, 1L, 2L, "2026-11-18", "11:05", 0.2, 96, 38.2, "borrador", "Línea 2, faltan fotos"));
        }
    }

    private Muestra muestra(Long centro, Long tren, Long linea, String fecha, String hora,
                            Double tramo, Integer conteo, Double peso, String estado, String obs) {
        Muestra m = new Muestra();
        m.setCodigo(UUID.randomUUID().toString());
        m.setCentroId(centro);
        m.setTrenId(tren);
        m.setLineaId(linea);
        m.setFecha(fecha);
        m.setHora(hora);
        m.setTramoMetros(tramo);
        m.setOperadorId(1L);
        m.setOperadorNombre("Operador Uno");
        m.setConteoIndividuos(conteo);
        m.setPesoMuestra(peso);
        m.setEstado(estado);
        m.setObservaciones(obs);
        return m;
    }
}
