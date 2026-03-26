package pe.gob.onpe.pradminbackend.security;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.sasa.dto.LoginDatosOutputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.LoginInputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.LoginPerfilesOutputDto;
import pe.gob.onpe.pradminbackend.sasa.service.UsuarioServicio;
import pe.gob.onpe.pradminbackend.security.dto.LoginRequest;
import pe.gob.onpe.pradminbackend.security.dto.UserContext;
import pe.gob.onpe.pradminbackend.security.exceptions.AuthMethodNotSupportedException;
import pe.gob.onpe.pradminbackend.security.utils.CryptoUtils;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AjaxAuthenticationProvider implements AuthenticationProvider {

    private final UsuarioServicio usuarioService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");

        String username = (String) authentication.getPrincipal();
        LoginRequest l = (LoginRequest) authentication.getCredentials();
        LoginDatosOutputDto u;
        

        try {

            LoginInputDto login = new LoginInputDto();
            login.setUsuario(username);
            login.setClave(l.getPassword()); // c.encrypt(l.getPassword())
            u = usuarioService.accederSistema(login);
            if (u == null)
            	throw new InsufficientAuthenticationException("Usuario/password incorrecto.");
            if (u.getDatos().getPerfiles().get(0) == null)
            	throw new InsufficientAuthenticationException("El usuario selecionado no cuenta con roles activos.");
        } catch (Exception e) {
            throw new AuthMethodNotSupportedException(e.getMessage());
        }

        List<GrantedAuthority> authoritiesp = new ArrayList<>();
        
        String abreviaturaPerfilp = null;
        LoginPerfilesOutputDto perfilp = null;
        String acronimoProcesop = null;
        String nombreCcp = null;
        String codigoCcp = null;
        if(u.getDatos()!=null && u.getDatos().getUsuario()!=null) {
        	acronimoProcesop = u.getDatos().getUsuario().getAcronimoProceso();
        	nombreCcp = u.getDatos().getUsuario().getNombreCentroComputo();
        	codigoCcp = u.getDatos().getUsuario().getCodigoCentroComputo();
        }
        if(u.getDatos()!=null && u.getDatos().getPerfiles()!=null) {
        	perfilp = u.getDatos().getPerfiles().get(0);
        	abreviaturaPerfilp = perfilp.getAbreviatura();
        }
        
        SimpleGrantedAuthority s = new SimpleGrantedAuthority(abreviaturaPerfilp);
        authoritiesp.add(s);

        UserContext userContext = UserContext.create(CryptoUtils.getHash(username, "SHA1"), u.getDatos().getUsuario().getNombres(), authoritiesp);
        userContext.setUsernameSinEncriptar(username);
        userContext.setUserId(u.getDatos().getUsuario().getIdUsuario());
        userContext.setAcronimoProceso(acronimoProcesop);
        userContext.setCodigoCentroComputo(codigoCcp);
        userContext.setNombreCentroComputo(nombreCcp);

        userContext.setPerfilId(u.getDatos().getPerfiles().get(0).getIdPerfil());

        return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
