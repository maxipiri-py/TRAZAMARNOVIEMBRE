package cl.aldemar.trazamar.backend.repository;

import cl.aldemar.trazamar.backend.model.Linea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineaRepository extends JpaRepository<Linea, Long> {
    List<Linea> findByActivoTrueOrderByNombre();
    List<Linea> findByTrenIdAndActivoTrueOrderByNombre(Long trenId);
}
