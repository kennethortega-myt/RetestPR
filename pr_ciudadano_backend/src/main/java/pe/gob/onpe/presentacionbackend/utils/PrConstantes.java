package pe.gob.onpe.presentacionbackend.utils;

import java.util.List;

public class PrConstantes {

    private PrConstantes() {
        throw new IllegalStateException("Util class");
    }

    public static final Integer LENGTH_BEARER = 7;

    public static final String TENANT_HEADER = "X-Tenant-Id";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String USERAGENT_HEADER = "User-Agent";
    public static final String USERAGENT_HEADER_VALUE = "ServerBackend";
    
    public static final String ESTADO_PROGRESO_CONTINUA = "1";
    public static final String ESTADO_PROGRESO_FINALIZA = "0";

    public static final String HEADER_STRING = "Authorization";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final String USER_ADMIN = "numeroDocumento";
    public static final String PERFIL_ADMIN_PR_VALOR = "ADM_PR";
    public static final String PERFIL_ADMIN_PR_ATRIBUTO = "per";

    public static final String VERSION_API = "/v1";
    protected  static final String[] APIS_LIBRES = new String[] {
            "/actas",
            "/actas/observadas",
            "/actas/buscar/mesa",
            "/actas/{id}",
            "/actas/{id}/agrupacion/{idAgru}/candidatos",
            "/actas/file",
            "/archivo",
            "/archivo/file/base64/",
            "/archivo/file/mock/",
            "/archivo/validar-servicio-fileserver",
            "/distrito-electoral/distritos",
            "/eleccion-diputado/participantes-ubicacion-geografica",
            "/eleccion-diputado/participantes-ubicacion-geografica-nombre",
            "/eleccion-diputado/participantes-por-candidato",
            "/eleccion-diputado/participantes-por-candidato-nombre",
            "/eleccion-diputado/organizacion-politica",
            "/eleccion-distrital/obtener",
            "/eleccion-distrital/obtener-participantes",
            "/eleccion-presidencial/participantes-ubicacion-geografica",
            "/eleccion-presidencial/participantes-ubicacion-geografica-nombre",
            "/eleccion-presidencial/participantes-organizacion-politica",
            "/senadores-distrital-multiple/participantes-ubicacion-geografica",
            "/senadores-distrital-multiple/participantes-candidato",
            "/senadores-distrital-multiple/participantes-candidato-organizacion",
            "/senadores-distrital-multiple/organizacion-politica",
            "/senadores-distrito-unico/participantes-ubicacion-geografica",
            "/senadores-distrito-unico/participantes-ubicacion-geografica-nombre",
            "/senadores-distrito-unico/participantes-por-candidato",
            "/senadores-distrito-unico/organizacion-politica",
            "/senadores-distrito-unico/participantes-realizar-busqueda",
            "/mesa/totales",
            "/padron/mesa",
            "/padron/mesa/{dni}",
            "/parlamento-andino/participantes-ubicacion-geografica",
            "/parlamento-andino/participantes-ubicacion-geografica-nombre",
            "/parlamento-andino/participantes-por-candidato",
            "/parlamento-andino/participantes-por-candidato-paginado",
            "/parlamento-andino/organizacion-politica",
            "/parlamento-andino/participantes-realizar-busqueda",
            "/revocatoria-distrital",
            "/revocatoria-distrital/participantes/{cargo}",
            "/participacion-ciudadana/departamentos",
            "/participacion-ciudadana/totales",
            "/participacion-ciudadana/ubigeos",
            "/participacion-ciudadana/mapa-calor",
            "/participacion-ciudadana/ubigeos-total",
            "/proceso/",
            "/proceso/proceso-electoral-activo",
            "/proceso/{id}/elecciones",
            "/proceso/tipo-ambito-por-acronimo/",
            "/proceso/{id-proceso}/tipo-ambito/",
            "/reportes/resumen-general",
            "/reportes/eleccion-parlamento-andino-geografica",
            "/reportes/eleccion-parlamento-andino-candidato",
            "/reportes/eleccion-parlamento-andino-organizacion",
            "/reportes/eleccion-parlamento-andino-resumen-general",
            "/reportes/eleccion-presidencial",
            "/reportes/eleccion-presidencial-organizacion",
            "/reportes/eleccion-presidencial-resumen-general",
            "/reportes/eleccion-diputados",
            "/reportes/eleccion-diputados-candidato",
            "/reportes/eleccion-diputados-candidato-organizacion",
            "/reportes/eleccion-diputados-resumen-general",
            "/reportes/validar-servicio-firma",
            "/reportes/eleccion-senadores-multiple",
            "/reportes/eleccion-senadores-multiple-candidato",
            "/reportes/eleccion-senadores-multiple-resumen-general",
            "/reportes/eleccion-senadores-multiple-candidato-organizacion",
            "/reportes/eleccion-senadores-unico-geografica",
            "/reportes/eleccion-senadores-unico-candidato",
            "/reportes/eleccion-senadores-unico-organizacion",
            "/reportes/eleccion-senadores-unico-resumen-general",
            "/reportes/participacion-ciudadana",
            "/resumen-general/totales",
            "/resumen-general/participantes",
            "/resumen-general/mapa-calor",
            "/resumen-general/mapa-calor-observadas",
            "/resumen-general/elecciones",
            "/trama-sce/recibir",
            "/ubigeos/departamentos",
            "/ubigeos/provincias",
            "/ubigeos/distritos",
            "/ubigeos/locales",
//            "/importar-ws",
            "/importar-paginado-ws",
            //"/maeimportar",
            "/maeimportar/validar-servicio-basedatos",
            "/api/auth/login",
            "/api/auth/restablecer-contrasenia",
            "/api/auth/actualizar-contrasenia",
            PrConstantes.VERSION_API + "/api/usuario/login",
            PrConstantes.VERSION_API + "/api/usuario/loginsc",
            PrConstantes.VERSION_API + "/api/recurso/apigoogle",
            PrConstantes.VERSION_API + "/api/auth/login",
            PrConstantes.VERSION_API + "/api/apigoogle"


    };

    protected static final String[] URL_WEB_LIBRES = new String[] {
    		"/ws/**",
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
            "/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html","/swagger-ui/index.html",
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
