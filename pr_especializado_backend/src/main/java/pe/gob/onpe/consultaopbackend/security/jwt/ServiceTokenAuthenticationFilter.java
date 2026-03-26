package pe.gob.onpe.consultaopbackend.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authToken = getJWTFromRequest(request);

        if (authToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = tokenProvider.validateAuthToken(authToken); // Usamos validateAuthToken que llama a
                                                                        // getClaimsFromAuthToken

            if (claims.containsKey("scopes")) {
                @SuppressWarnings("unchecked")
                List<String> scopes = claims.get("scopes", List.class);
                if (scopes.contains("ROLE_SERVICE")) {
                    // Es un token de servicio, lo autenticamos y terminamos la cadena
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            claims.getSubject(),
                            null,
                            scopes.stream().map(SimpleGrantedAuthority::new).toList());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("Token de servicio autenticado para el sujeto: {}", claims.getSubject());
                }
            }
        } catch (Exception e) {
            // Si el token está mal formado o expirado, el JWTAuthenticationFilter se
            // encargará de reportar el error.
            // Aquí solo registramos un debug para saber que este filtro lo intentó primero.
            log.debug("ServiceTokenFilter no pudo procesar el token. Delegando. Error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
