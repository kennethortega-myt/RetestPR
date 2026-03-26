package pe.gob.onpe.pradminbackend.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.security.dto.UserContext;
import pe.gob.onpe.pradminbackend.security.enums.Scopes;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenFactory {

    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    private final JwtSettings settings;

    public String createAccessJwtToken(UserContext userContext, String validator, String sasaToken) {
        if (StringUtils.isBlank(userContext.getUsername()))
            throw new IllegalArgumentException("Cannot create JWT Token without username");

        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty())
            throw new IllegalArgumentException("User doesn't have any privileges");


        Map<String, Object> claims = new HashMap<>();

        claims.put("scopes", userContext.getAuthorities().stream().map(Object::toString).toList());
        claims.put("usr", userContext.getUsernameSinEncriptar());
        claims.put("cll", validator);
        claims.put("dil", userContext.getUserId());
        claims.put("epd", userContext.getOdpe());
        claims.put("lac", userContext.getLocal());
        claims.put("idp", userContext.getPerfilId());
        claims.put("oip", userContext.getCentroAcopio());

        claims.put("apr", userContext.getAcronimoProceso());
        claims.put("ncc", userContext.getNombreCentroComputo());
        claims.put("ccc", userContext.getCodigoCentroComputo());
        claims.put("per", userContext.getAuthorities()!=null ? userContext.getAuthorities().get(0).getAuthority() : null);
        claims.put("sasa_token", sasaToken); 
        claims.put("name", userContext.getName());
        claims.put("perfil", userContext.getPerfil());// userContext.getAuthorities().get(0).getAuthority().)
        claims.put("idSession", userContext.getIdSession());

        long nowMillis = System.currentTimeMillis();
        Date horaActual = new Date(nowMillis);
        long expMillis = nowMillis + (settings.getTokenExpirationTime() * 60 * 1000); // Convertir minutos a milisegundos
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .claims(claims)
                .subject(userContext.getUsername())
                .issuer(settings.getTokenIssuer())
                .issuedAt(horaActual)
                .expiration(exp)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(settings.getTokenSigningKey())))
                .compact();
    }

    public String createRefreshToken(UserContext userContext, String validator, String sasaToken) {
        if (StringUtils.isBlank(userContext.getUsername())) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }


        Map<String, Object> claims = new HashMap<>();
        claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.authority()));
        claims.put("usr", userContext.getName());
        claims.put("cll", validator);
        claims.put("date", formatoFecha.format(new Date()));
        claims.put("per", userContext.getAuthorities().get(0).getAuthority());
        claims.put("sasa_token", sasaToken);

        long nowMillis = System.currentTimeMillis();
        Date horaActual = new Date(nowMillis);
        long expMillis = nowMillis + (settings.getRefreshTokenExpTime() * 60 * 1000); // Convertir minutos a milisegundos
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .claims(claims)
                .subject(userContext.getUsername())
                .issuedAt(horaActual)
                .expiration(exp)
                .issuer(settings.getTokenIssuer())
                .id(UUID.randomUUID().toString())
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(settings.getTokenSigningKey())))
                .compact();
    }

}
