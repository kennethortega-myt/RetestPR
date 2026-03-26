package pe.gob.onpe.consultaopbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.consultaopbackend.utils.ConstantesComunes;
import pe.gob.onpe.consultaopbackend.utils.Funciones;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class JWTTokenProvider {

	private final JwtSettings settings;

    private final Funciones funciones;

    public Claims validateAuthToken(final String token) {
        // just parsing correctly means its valid
        return getClaimsFromAuthToken(token);
    }

    // JWT special Claims
    Claims getClaimsFromAuthToken(String token) {
    	try {
            return Jwts.parser()
            		.verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(settings.getTokenSigningKey())))
    				.build()
        			.parseSignedClaims(token)
    				.getPayload();
        } catch (ExpiredJwtException e) {
            // El token ha expirado
            log.warn("Intento de acceso con token expirado: {}", e.getMessage());
            throw e; // Relanzamos la excepción original para que el filtro la capture.
        } catch (SecurityException e) {
            // Firma del token no válida
            log.error("Firma del token no válida: " + e.getMessage());
            throw new SecurityException("Invalid JWT signature", e);
        } catch (MalformedJwtException e) {
            // El token está mal formado
            log.error("El token está mal formado: " + e.getMessage());
            throw new SecurityException("Invalid JWT token", e);
        } catch (Exception e) {
            // Otros errores
            log.error("Error al validar el token JWT: " + e.getMessage());
            throw new SecurityException("JWT validation failed", e);
        }
    }

    public String obtenerSoloToken(String token){
        return token.substring(ConstantesComunes.BEARER_TOKEN_PREFIX.length());
    }

    public Map<String, Object> obtenerPayload(String token){
        String tokenOnly = obtenerSoloToken(token);
        Claims claims = getClaimsFromAuthToken(tokenOnly);
        return Funciones.getMapFromIoJsonwebtokenClaims(claims);
    }

    public String generarRefreshToken(final Map<String, Object> claims, String ruc){
        long nowMillis = System.currentTimeMillis();
        Date horaActual = new Date(nowMillis);

        long expMillis = nowMillis + ConstantesComunes.EXPIRATION_TIME_MILISECONDS;
        Date exp = new Date(expMillis);

        return Jwts.builder().claims(claims).subject(ruc).issuedAt(horaActual).expiration(exp).signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(settings.getTokenSigningKey()))).compact();
    }

}
