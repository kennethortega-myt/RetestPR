package pe.gob.onpe.presentacionbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8099}")
    private String appPort;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Value("${app.server.deploydomain:}")
    private String deployDomain;

    @Value("${app.server.iddevtunnel:}")
    private String idDevTunnel;

    @Bean
    public OpenAPI customOpenAPI() {
        List<Server> servers = new ArrayList<>();

        // Siempre localhost
        servers.add(new Server()
                .url(UriComponentsBuilder.newInstance()
                        .scheme("http")
                        .host("localhost")
                        .port(appPort)
                        .path(contextPath)
                        .toUriString())
                .description("LocalHost"));

        // Solo agregar deploy si está configurado
        if (deployDomain != null && !deployDomain.isBlank()) {
            servers.add(new Server()
                    .url(UriComponentsBuilder.newInstance()
                            .scheme("https")
                            .host(deployDomain)
                            .path(contextPath)
                            .toUriString())
                    .description("Deploy"));
        }

        // Solo agregar DevTunnel si está configurado
        if (idDevTunnel != null && !idDevTunnel.isBlank()) {
            servers.add(new Server()
                    .url(UriComponentsBuilder.newInstance()
                            .scheme("https")
                            .host(idDevTunnel + "-" + appPort + ".brs.devtunnels.ms")
                            .path(contextPath)
                            .toUriString())
                    .description("DevTunnel"));
        }

        return new OpenAPI()
                .info(new Info()
                        .title("API de Módulo Ciudadano")
                        .version("1.0.0")
                        .description("Documentación de la API utilizando Springdoc OpenAPI"))
                .servers(servers);
    }
}
