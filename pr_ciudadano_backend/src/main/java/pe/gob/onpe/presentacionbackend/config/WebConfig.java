package pe.gob.onpe.presentacionbackend.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a todas las rutas
                .allowedOrigins(
                        "http://localhost:4200",
                        "http://localhost:8099",
                        "https://*.devtunnels.ms",
                        "https://*.brs.devtunnels.ms"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Cabeceras permitidas
                .exposedHeaders("Content-Disposition")
                .allowCredentials(true); // Permitir envío de credenciales (cookies, etc.)
    }
}