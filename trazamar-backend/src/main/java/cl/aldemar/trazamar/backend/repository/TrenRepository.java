package cl.aldemar.trazamar.backend.repository;

import cl.aldemar.trazamar.backend.model.Tren;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrenRepository extends JpaRepository<Tren, Long> {
    List<Tren> findByActivoTrueOrderByNombre();
    List<Tren> findByCentroIdAndActivoTrueOrderByNombre(Long centroId);
}
