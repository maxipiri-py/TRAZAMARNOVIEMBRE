package cl.aldemar.trazamar.backend.repository;

import cl.aldemar.trazamar.backend.model.Centro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CentroRepository extends JpaRepository<Centro, Long> {
    List<Centro> findByActivoTrueOrderByNombre();
    boolean existsByCodigoIgnoreCase(String codigo);
}
