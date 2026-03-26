package pe.gob.onpe.pradminbackend.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenDecoderDelete {
	
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
