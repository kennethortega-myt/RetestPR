package pe.gob.onpe.pradminbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pe.gob.onpe.pradminbackend.sasa.dto.LoginDatosOutputDto;
import pe.gob.onpe.pradminbackend.sasa.dto.LoginInputDto;
import pe.gob.onpe.pradminbackend.sasa.service.UsuarioServicio;

@Component
@Profile("dev") // ⚠️ Solo en perfil dev
public class SwaggerDevTokenProvider {

    private final UsuarioServicio usuarioServicio;

    @Autowired
    public SwaggerDevTokenProvider(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    private String cachedToken;

    public String getToken() {
        if (cachedToken == null) {
            try {
                LoginInputDto input = new LoginInputDto();
                input.setUsuario("USER_PILOTO");  // 👈 usuario de pruebas
                input.setClave("12345678aA$");      // 👈 clave de pruebas

                LoginDatosOutputDto datos = usuarioServicio.accederSistema(input);
                cachedToken = "Bearer " + datos.getDatos().getUsuario().getToken();
            } catch (Exception e) {
                return null;
            }
        }
        return cachedToken;
    }
}

