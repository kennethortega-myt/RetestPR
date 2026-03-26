package pe.gob.onpe.pradminbackend.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

import pe.gob.onpe.pradminbackend.config.SwaggerDevAuthFilter;
import pe.gob.onpe.pradminbackend.security.jwt.JWTAuthenticationFilter;
import pe.gob.onpe.pradminbackend.security.jwt.ServiceTokenAuthenticationFilter; // Importar el nuevo filtro
import pe.gob.onpe.pradminbackend.utils.PrConstantes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JWTAuthenticationFilter authenticationFilter;
    private final ServiceTokenAuthenticationFilter serviceTokenAuthenticationFilter; // Inyectar el nuevo filtro

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
        @Autowired(required = false) SwaggerDevAuthFilter swaggerDevAuthFilter) throws Exception {

        var builder = http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                //.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/error/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/ws").denyAll();
                    auth.requestMatchers(HttpMethod.GET, "/ws/").denyAll();
                    auth.requestMatchers(PrConstantes.getApisLibresArray()).permitAll();
                    auth.requestMatchers(PrConstantes.getUrlWebLibresArray()).permitAll();
                    auth.anyRequest().authenticated();
                })
                //.addFilterBefore(new CORSFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(serviceTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Añadir el nuevo filtro
                .addFilterAfter(authenticationFilter, ServiceTokenAuthenticationFilter.class); // Ejecutar el filtro original después

        // 👇 Solo en dev existe swaggerDevAuthFilter, en otros perfiles es null
        if (swaggerDevAuthFilter != null) {
            builder.addFilterBefore(swaggerDevAuthFilter, ServiceTokenAuthenticationFilter.class);
        }

        return builder.build();
    }

}
