package pe.gob.onpe.pradminbackend.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import pe.gob.onpe.pradminbackend.security.dto.UserContext;



@Component
@SuppressWarnings("unchecked")
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtAuthenticate jwtAuthenticate;

    @Autowired
    public JwtAuthenticationProvider(final JwtAuthenticate jwtAuthenticate) {
        this.jwtAuthenticate = jwtAuthenticate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserContext context = jwtAuthenticate.authenticate(authentication);
        return new JwtAuthenticationToken(context, context.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
