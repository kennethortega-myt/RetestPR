package pe.gob.onpe.consultaopbackend.utils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConstantesComunes {

    private ConstantesComunes() {
        // Private constructor to hide the implicit public one
    }

    public static final String HEADER_STRING = "Authorization";
    public static final String BEARER_LIST_SCHEME = "bearerAuth";
    public static final String BEARER_TOKEN_PREFIX = "Bearer";
    public static final String BEARER_FORMAT = "JWT";
    public static final long EXPIRATION_TIME_SECONDS = (long)60 * 60;
    public static final long EXPIRATION_TIME_MILISECONDS = (long)1000 * 60 *
    60;

    public static final Long CODIGO_ELECCION_CERO = 0L;

    public static final Integer TIPO_ARCHIVO_ACTA_ESCRUTINIO = 1;
    public static final Integer TIPO_ARCHIVO_ACTA_INSTALACION_SUFRAGIO = 2;
    public static final Integer TIPO_ARCHIVO_ACTA_INSTALACION = 3;
    public static final Integer TIPO_ARCHIVO_ACTA_SUFRAGIO = 4;
    public static final Integer TIPO_ARCHIVO_RESOLUCION = 5;

    public static final int TIPO_GENERACION_TIEMPO = 1;
    public static final int TIPO_GENERACION_PORCENTAJE = 2;
    public static final int ESTADO_INACTIVO = 0;
    public static final int ESTADO_ACTIVO = 1;
    public static final int MINUTOS_POR_HORA = 60;

    public static final String DESCONOCIDO = "Desconocido";

    public static final String ESTADO_ACTAS = "estadoActa";
    public static final String ID_DISTRITO_ELECTORAL = "idDistritoElectoral";

    private static final Map<Integer, String> DESCRIPCIONES = Map.of(
        TIPO_ARCHIVO_ACTA_ESCRUTINIO, "ACTA DE ESCRUTINIO",
        TIPO_ARCHIVO_ACTA_INSTALACION_SUFRAGIO, "ACTA DE INSTALACIÓN Y SUFRAGIO",
        TIPO_ARCHIVO_ACTA_INSTALACION, "ACTA DE INSTALACIÓN",
        TIPO_ARCHIVO_ACTA_SUFRAGIO, "ACTA DE SUFRAGIO",
        TIPO_ARCHIVO_RESOLUCION, "RESOLUCIÓN"
    );

    public static String obtenerDescripcion(Integer tipo, AtomicInteger counter) {
        if (tipo == null) {
            return "Tipo no mapeado en PR";
        }
        if (tipo.equals(TIPO_ARCHIVO_RESOLUCION)) {
            return DESCRIPCIONES.get(tipo) + " " + counter.getAndDecrement();
        }
        return DESCRIPCIONES.getOrDefault(tipo, "Tipo no mapeado en PR");
    }

}
