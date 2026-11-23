package cl.aldemar.trazamar.backend.dto;

import cl.aldemar.trazamar.backend.model.Usuario;

// Lo que responde el backend al iniciar sesión: el token y los datos del usuario (con su rol)
public record LoginResponse(String token, Usuario usuario) {
}
