package pe.gob.onpe.consultaopcron.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenFactory {

    private final JwtSettings settings;

    /**
     * Crea un JWT para la comunicación entre servicios (Server-to-Server).
     */
    public String createServiceToServerToken() {
        Map<String, Object> claims = new HashMap<>();
        // Define un scope o permiso específico para la comunicación entre servicios
        claims.put("scopes", Collections.singletonList("ROLE_SERVICE"));

        long nowMillis = System.currentTimeMillis();
        Date horaActual = new Date(nowMillis);
        // El token será válido por 5 minutos
        long expMillis = nowMillis + (5 * 60 * 1000);
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .claims(claims)
                .subject("consulta-op-cron-service") // Identifica al servicio que genera el token
                .issuer(settings.getTokenIssuer())
                .issuedAt(horaActual)
                .expiration(exp)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(settings.getTokenSigningKey())))
                .compact();
    }
}
