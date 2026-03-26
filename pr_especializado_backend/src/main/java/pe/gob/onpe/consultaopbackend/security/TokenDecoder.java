package pe.gob.onpe.consultaopbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class TokenDecoder {

	@Value("${security.jwt.tokenSigningKey}")
    private String tokenSigningKey;

	public Claims decodeToken(String token) {

        return Jwts.parser()
        		.verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSigningKey)))
                .build()
                .parseSignedClaims(token.replace("\"", ""))
                .getPayload();
    }
	
}
