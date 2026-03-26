package pe.gob.onpe.consultaopbackend.utils;

import java.util.List;

public class PrConstantes {

    private PrConstantes() {
        throw new IllegalStateException("PrConstantes class");
    }

    public static final Integer LENGTH_BEARER = 7;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String HEADER_IDSESSION = "IdSession";
    public static final Integer N_SESSION_UNICA = 1;

    public static final String HEADER_STRING = "Authorization";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final String VERSION_API = "/v1";

    public static final String USER_NUMERO_DOCUMENTO = "dato4";
    public static final String USER_ID = "sub";
    protected static final String[] APIS_LIBRES = new String[] {
            "/health",
            "/api/auth/login",
            "/api/auth/restablecer-contrasenia",
            "/api/auth/cerrar-sesion-activa",
            "/recurso/apigoogle",
            "/procesamientoActas/eliminarCarpetaDescargaActas"
    };

    protected static final String[] URL_WEB_LIBRES = new String[] {
            "/swagger-ui",
            "/swagger-ui.html",
            "/swagger-ui/swagger-ui.css",
            "/swagger-ui/index.css",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui/swagger-initializer.js",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/swagger-ui/favicon-32x32.png",
            "/swagger-ui/favicon-16x16.png",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/v3/api-docs/swagger-config",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-resources/configuration/security",
            "/swagger-resources/configuration/ui",
            "/swagger-ui/index.html**", "/webjars/**", "/actuator/health",
            "/webjars",
            "/swagger-resources",
            "/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/webjars/**" };

    public static List<String> getApisLibres() {
        return List.of(APIS_LIBRES);
    }

    public static String[] getApisLibresArray() {
        return APIS_LIBRES;
    }

    public static List<String> getUrlWebLibres() {
        return List.of(URL_WEB_LIBRES);
    }

    public static String[] getUrlWebLibresArray() {
        return URL_WEB_LIBRES;
    }

}
