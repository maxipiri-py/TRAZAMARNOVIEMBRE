package cl.aldemar.trazamar.backend.repository;

import cl.aldemar.trazamar.backend.model.Muestra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * REPOSITORY = el "bibliotecario" de la tabla muestras.
 * Con solo extender JpaRepository ya tenemos: findAll, findById, save, deleteById, count...
 * Spring escribe el SQL por nosotros a partir del nombre del método.
 */
public interface MuestraRepository extends JpaRepository<Muestra, Long> {

    // SELECT * FROM muestras WHERE codigo = ?
    Optional<Muestra> findByCodigo(String codigo);
}
