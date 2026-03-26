package pe.gob.onpe.consultaopbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import pe.gob.onpe.consultaopbackend.utils.ConstantesComunes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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

    private List<Server> buildServers() {
        List<Server> servers = new ArrayList<>();
        servers.add(new Server()
                .url(UriComponentsBuilder.newInstance()
                        .scheme("http")
                        .host("localhost")
                        .port(appPort)
                        .path(contextPath)
                        .toUriString())
                .description("LocalHost"));

        if (deployDomain != null && !deployDomain.isBlank()) {
            servers.add(new Server()
                    .url(UriComponentsBuilder.newInstance()
                            .scheme("https")
                            .host(deployDomain)
                            .path(contextPath)
                            .toUriString())
                    .description("Deploy"));
        }

        if (idDevTunnel != null && !idDevTunnel.isBlank()) {
            servers.add(new Server()
                    .url(UriComponentsBuilder.newInstance()
                            .scheme("https")
                            .host(idDevTunnel + "-" + appPort + ".brs.devtunnels.ms")
                            .path(contextPath)
                            .toUriString())
                    .description("DevTunnel"));
        }
        return servers;
    }

    private OpenAPI buildOpenAPI(String descriptionExtra, boolean withJwt) {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("API de Módulo Especializado")
                        .version("1.0.0")
                        .description("Documentación de la API utilizando Springdoc OpenAPI"))
                .servers(buildServers());

        if (withJwt) {
            openAPI.addSecurityItem(new SecurityRequirement().addList(ConstantesComunes.BEARER_LIST_SCHEME))
                    .components(new Components().addSecuritySchemes(ConstantesComunes.BEARER_LIST_SCHEME,
                            new SecurityScheme()
                                    .name(ConstantesComunes.HEADER_STRING)
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme(ConstantesComunes.BEARER_TOKEN_PREFIX)
                                    .bearerFormat(ConstantesComunes.BEARER_FORMAT)
                                    .description("Usa 'Authorize' para ingresar tu token JWT" +
                                            (descriptionExtra != null ? "\n" + descriptionExtra : ""))));
        }

        return openAPI;
    }

    // Swagger normal (QA, Prod, etc.)
    @Bean
    @Profile("!dev")
    public OpenAPI customOpenAPI() {
        return buildOpenAPI(null, true);
    }

    // Swagger con token automático (solo en DEV y si existe el provider)
    @Bean
    @Profile("dev")
    @ConditionalOnBean(SwaggerDevTokenProvider.class)
    public OpenAPI customOpenAPIWithToken(SwaggerDevTokenProvider tokenProvider) {
        return buildOpenAPI("Token automático en DEV:\n" + tokenProvider.getToken(), true);
    }

    // Swagger en DEV pero sin JWT (no existe el provider)
    @Bean
    @Profile("dev")
    @ConditionalOnMissingBean(SwaggerDevTokenProvider.class)
    public OpenAPI customOpenAPIWithoutJwt() {
        return buildOpenAPI("Este proyecto no requiere autenticación JWT", false);
    }
}
