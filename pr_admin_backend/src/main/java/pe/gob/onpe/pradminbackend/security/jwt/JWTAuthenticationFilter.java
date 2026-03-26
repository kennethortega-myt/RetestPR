package pe.gob.onpe.pradminbackend.security.jwt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pe.gob.onpe.pradminbackend.recaptcha.properties.SeguridadValidarProperties;
import pe.gob.onpe.pradminbackend.redis.RedisService;
import pe.gob.onpe.pradminbackend.security.exceptions.ExceptionResponse;
import pe.gob.onpe.pradminbackend.security.properties.CrossOriginProperties;
import pe.gob.onpe.pradminbackend.utils.PrConstantes;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {


    private final JWTTokenProvider tokenProvider;
    private final SeguridadValidarProperties seguridadValidarProperties;
    private final CrossOriginProperties crossOriginProperties;
    private final RedisService redisService;

    @Value("${pr_admin_access_key}")
    private String prAdminAccessKey;

    public List<String> getAllowedOrigins() {
        String urls = crossOriginProperties.urls();
        return Arrays.asList(urls.split(","));
    }

    public String getCrossOrigin(HttpServletRequest request) {
        if( !Boolean.parseBoolean(crossOriginProperties.habilitado())) {
            return "*";
        }
        String origin = request.getHeader("origin");
        for(String tmp : getAllowedOrigins()){
            if(origin != null && tmp.trim().contains(origin)){
                return origin;
            }
        }
        return getAllowedOrigins().get(0);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        this.setHeaders(request, response);

        if (this.isPreflightRequest(request, response)) {
            return;
        }

        String uri = request.getRequestURI();
        // Manejo especial para rutas WebSocket
        if (uri.startsWith(request.getContextPath()+"/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            this.handleRequest(request, response, filterChain);
        } catch (ExpiredJwtException e) {
            handleExpiredToken(request, response, e);
        } catch (UnsupportedJwtException e) {
            log.error("No se puede detectar la clave del token.", e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("El token esta mal formado.", e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            validarError(response, "Ocurrió un error interno.");
        }
    }

    private void setHeaders(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("X-FRAME-OPTIONS", "DENY");
        response.setHeader("Access-Control-Allow-Origin", this.getCrossOrigin(request));
        response.setHeader("Access-Control-Allow-Methods", "GET,POST");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "36000");
    }

    private boolean isPreflightRequest(HttpServletRequest request, HttpServletResponse response) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        return false;
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException{
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth != null && existingAuth.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI();

        if (Boolean.TRUE.equals(esUrlLibre(uri, request))) {
            filterChain.doFilter(request, response);
        }else {
            if (Boolean.parseBoolean(seguridadValidarProperties.validar())) {
                if (checkJWTToken(request)) {
                    String jwt = getJWTFromRequest(request);

                    Boolean isBlacklisted = redisService.isBlacklisted(jwt);

                    if(isBlacklisted != null && isBlacklisted){
                        log.info("Token revocado: {}", jwt);
                        SecurityContextHolder.clearContext();
                        validarError(response, "El token ha sido revocado.");
                        return;
                    }

                    Claims claims = tokenProvider.validateAuthToken(jwt);

                    // --- INICIO DE LA NUEVA LÓGICA DE VALIDACIÓN ---
                    String apiKeyClaim = claims.get("apiKey", String.class);

                    if (apiKeyClaim != null) {
                        // Es un token de servicio a servicio
                        if (Objects.equals(apiKeyClaim, prAdminAccessKey)) {
                            Authentication auth = new UsernamePasswordAuthenticationToken("service-account", null, null);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            filterChain.doFilter(request, response);
                        } else {
                            log.warn("API Key en token JWT no válida: {}", apiKeyClaim);
                            validarError(response, "API Key no válida.");
                        }
                        return; // Importante: terminar el procesamiento aquí
                    }
                    // --- FIN DE LA NUEVA LÓGICA DE VALIDACIÓN ---


                    if (!validarIdSession(claims, request, response)) {
                        SecurityContextHolder.clearContext();
                        return;
                    }

                    if (claims.get(PrConstantes.USER_NUMERO_DOCUMENTO) != null || claims.get(PrConstantes.USER_ID) != null) {
                        Authentication auth = new UsernamePasswordAuthenticationToken(claims.get(PrConstantes.USER_ID),null,null);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        filterChain.doFilter(request, response);
                    } else {
                        log.info("El token no cuenta con la clave principal.");
                        SecurityContextHolder.clearContext();
                        validarError(response, "El token no cuenta con la clave principal.");
                    }
                }else {
                    log.info(request.getRequestURI()+": Se requiere token para acceder al servicio.");
                    SecurityContextHolder.clearContext();
                    validarError(response, "Se requiere token para acceder al servicio.");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            }else {
                log.info("PR-ADMIN - INGRESANDO SIN TOKEN");
                log.info("PR-ADMIN - request"+request.getRequestURI());
                filterChain.doFilter(request, response);
            }
        }
    }

    private boolean validarIdSession(Claims claims, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tokenIdSessionHash = claims.get("idSession", String.class);
        String headerIdSession = request.getHeader(PrConstantes.HEADER_IDSESSION);

        if (tokenIdSessionHash == null) {
            log.info("El token no contiene IdSession");
            validarError(response, "El token no contiene IdSession");
            return false;
        }

        if (headerIdSession == null) {
            log.info("No se proporcionó IdSession en la cabecera");
            validarError(response, "Se requiere IdSession en la cabecera");
            return false;
        }

        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        boolean ok = enc.matches(headerIdSession, tokenIdSessionHash);

        if (!ok) {
            log.info("IdSession de cabecera no coincide con el del token. Token: {}, Header: {}",
                    tokenIdSessionHash, headerIdSession);
            validarError(response, "IdSession inválido o no coincide con el token");
            return false;
        }

        return true;
    }

    private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response, ExpiredJwtException e) throws IOException {
        String isRefreshToken = request.getHeader("isRefreshToken");
        String requestURL = request.getRequestURL().toString();
        if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refreshtoken")) {
            allowForRefreshToken(e, request);
        } else {
            request.setAttribute("exception", e);
            log.error("El token ha expirado.", e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private void validarError(HttpServletResponse response, String mensaje) throws IOException {
        ExceptionResponse error = new ExceptionResponse();
        error.setMensaje(mensaje);
        error.setMensajeInteno(mensaje);
        error.setResultado(-1);

        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String errorJson = gson.toJson(error);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorJson);
    }

    private void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                null, null, null);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        request.setAttribute("claims", ex.getClaims());
    }

    private Boolean esUrlLibre(String url, HttpServletRequest request) {
        String urlTmp = "";
        for (String urlLibre : PrConstantes.getUrlWebLibres()) {
            urlTmp = request.getContextPath() + urlLibre;
            if (url.contains(urlTmp)) {
                return true;
            }
        }
        for (String urlLibre : PrConstantes.getApisLibres()) {
            urlTmp = request.getContextPath() + urlLibre;
            if (url.contains(urlTmp)) {
                return true;
            }
        }
        return false;
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader(PrConstantes.HEADER_STRING);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(PrConstantes.BEARER_TOKEN_PREFIX)) {
            return bearerToken.substring(PrConstantes.BEARER_TOKEN_PREFIX.length());
        }
        throw new MalformedJwtException("Authentication Header doesn't have Bearer Token");
    }

    private boolean checkJWTToken(HttpServletRequest request) {
        final String authenticationHeader = request.getHeader(PrConstantes.HEADER_STRING);
        return authenticationHeader != null && authenticationHeader.startsWith(PrConstantes.BEARER_TOKEN_PREFIX);
    }
}
