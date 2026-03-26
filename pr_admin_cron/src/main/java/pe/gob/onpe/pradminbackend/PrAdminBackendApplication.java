package pe.gob.onpe.pradminbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PrAdminBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrAdminBackendApplication.class, args);
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Registrar el módulo para manejar correctamente los tipos de fecha y hora de Java 8
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}
