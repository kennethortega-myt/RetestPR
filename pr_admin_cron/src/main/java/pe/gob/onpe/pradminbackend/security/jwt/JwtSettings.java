package pe.gob.onpe.pradminbackend.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JwtSettings {

    @Value("${security.jwt.tokenExpirationTime}")
    private Integer tokenExpirationTime;
    @Value("${security.jwt.tokenIssuer}")
    private String tokenIssuer;
    @Value("${security.jwt.tokenSigningKey}")
    private String tokenSigningKey;
    @Value("${security.jwt.refreshTokenExpTime}")
    private Integer refreshTokenExpTime;

}