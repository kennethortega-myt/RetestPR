package pe.gob.onpe.consultaopbackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import pe.gob.onpe.consultaopbackend.utils.ConstantesComunes;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Profile("dev") // ⚠️ solo en perfil dev
public class SwaggerDevAuthFilter extends OncePerRequestFilter {

    private final SwaggerDevTokenProvider tokenProvider;

    public SwaggerDevAuthFilter(SwaggerDevTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String devToken = tokenProvider.getToken();
        request = new AuthRequestWrapper(request, devToken);
        filterChain.doFilter(request, response);
    }

    /**
     * Wrapper interno para inyectar el header Authorization con el token de dev.
     */
    private static class AuthRequestWrapper extends HttpServletRequestWrapper {
        private final String token;

        public AuthRequestWrapper(HttpServletRequest request, String token) {
            super(request);
            this.token = token;
        }

        @Override
        public String getHeader(String name) {
            if (ConstantesComunes.HEADER_STRING.equalsIgnoreCase(name)) {
                return token;
            }
            return super.getHeader(name);
        }
    }
}
