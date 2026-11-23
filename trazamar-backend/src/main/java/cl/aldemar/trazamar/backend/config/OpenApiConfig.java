package cl.aldemar.trazamar.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Título de Swagger + botón "Authorize" (candado) para pegar el token del login.
 * Así se pueden probar en Swagger los endpoints que exigen un rol.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI trazamarOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("TrazaMar API — AquaSample")
                        .version("1.0.0")
                        .description("Backend de NotFound-Devs para ALDEMAR SpA: registro, revisión y trazabilidad de muestras de choritos. Datos ficticios. "
                                + "Usuarios demo (clave 1234): OP-01, SUP-01, AN-01, ADMIN-01."))
                .components(new Components().addSecuritySchemes("token",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")))
                .addSecurityItem(new SecurityRequirement().addList("token"));
    }
}
