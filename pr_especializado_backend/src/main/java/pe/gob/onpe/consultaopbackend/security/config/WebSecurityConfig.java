package pe.gob.onpe.consultaopbackend.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.gob.onpe.consultaopbackend.security.jwt.JWTAuthenticationFilter;
import pe.gob.onpe.consultaopbackend.security.jwt.ServiceTokenAuthenticationFilter;
import pe.gob.onpe.consultaopbackend.utils.PrConstantes;
import pe.gob.onpe.consultaopbackend.config.SwaggerDevAuthFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JWTAuthenticationFilter authenticationFilter;
    private final ServiceTokenAuthenticationFilter serviceTokenAuthenticationFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
            @Autowired(required = false) SwaggerDevAuthFilter swaggerDevAuthFilter) throws Exception {

        var builder = http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/error/**").permitAll();
                    auth.requestMatchers(PrConstantes.getApisLibresArray()).permitAll();
                    auth.requestMatchers(PrConstantes.getUrlWebLibresArray()).permitAll();
                    auth.anyRequest().authenticated();
                })

                .addFilterBefore(serviceTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(authenticationFilter, ServiceTokenAuthenticationFilter.class);

        // Solo en dev existe swaggerDevAuthFilter, en otros perfiles es null
        if (swaggerDevAuthFilter != null) {
            builder.addFilterBefore(swaggerDevAuthFilter, JWTAuthenticationFilter.class);
        }

        return builder.build();
    }
}
