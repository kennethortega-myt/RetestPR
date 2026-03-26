package pe.gob.onpe.pradminbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.pradminbackend.utils.ConstantesComunes;

import org.springframework.stereotype.Component;


@Component
@Log4j2
@RequiredArgsConstructor
public class JWTTokenProvider {

	private final JwtSettings settings;

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
            log.error("El token ha expirado: " + e.getMessage());
            throw new SecurityException("Token expired", e);
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
}
