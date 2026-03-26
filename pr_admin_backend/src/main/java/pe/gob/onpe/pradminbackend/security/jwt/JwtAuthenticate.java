package pe.gob.onpe.pradminbackend.security.jwt;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.security.dto.UserContext;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;


@Component
@RequiredArgsConstructor
public class JwtAuthenticate {


    private final JwtSettings jwtSettings;

    public UserContext authenticate(Authentication authentication) {
        RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();
        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(jwtSettings.getTokenSigningKey());
        String subject = jwsClaims.getBody().getSubject();
        List<String> scopes = jwsClaims.getBody().get("scopes", List.class);
        String name = jwsClaims.getBody().get("usr", String.class);
        String validator = jwsClaims.getBody().get("cll", String.class);
        List<GrantedAuthority> authorities = scopes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return UserContext.create(subject, name, validator, authorities);
    }
	
}
