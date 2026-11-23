package cl.aldemar.trazamar.backend.repository;

import cl.aldemar.trazamar.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsernameIgnoreCase(String username);
    Optional<Usuario> findByToken(String token);
    List<Usuario> findAllByOrderByNombreCompleto();
}
