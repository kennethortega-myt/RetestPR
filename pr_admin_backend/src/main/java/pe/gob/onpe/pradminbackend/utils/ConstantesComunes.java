package pe.gob.onpe.pradminbackend.utils;

public class ConstantesComunes {

    private ConstantesComunes() {
        throw new IllegalStateException("ConstantesComunes");
    }

    public static final String HEADER_STRING = "Authorization";
    public static final String BEARER_LIST_SCHEME = "bearerAuth";
    public static final String BEARER_TOKEN_PREFIX = "Bearer";
    public static final String BEARER_FORMAT = "JWT";
    public static final long EXPIRATION_TIME_SECONDS = (long)60 * 60;
    public static final long EXPIRATION_TIME_MILISECONDS = (long)1000 * 60 * 60;

    public static final Integer TIPO_ARCHIVO_ESCRUTINIO = 0;
    public static final Integer TIPO_ARCHIVO_INSTALACION_SUFRAGIO = 1;
    public static final Integer TIPO_ARCHIVO_RESOLUCION = 2;

    public static final Integer TIPO_ARCHIVO_ACTA_ESCRUTINIO = 1;
    public static final Integer TIPO_ARCHIVO_ACTA_INSTALACION_SUFRAGIO = 2;
    public static final Integer TIPO_ARCHIVO_ACTA_INSTALACION = 3;
    public static final Integer TIPO_ARCHIVO_ACTA_SUFRAGIO = 4;

    public static final Long CODIGO_ELECCION_CERO = 0L;

    public static final String USUARIO_AUTOMATICO = "automatico";


}
