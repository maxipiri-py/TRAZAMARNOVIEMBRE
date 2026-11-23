package cl.aldemar.trazamar.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de partida del backend: al ejecutar este main, Spring Boot levanta el servidor en el puerto 8080
@SpringBootApplication
public class TrazamarBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrazamarBackendApplication.class, args);
    }
}
