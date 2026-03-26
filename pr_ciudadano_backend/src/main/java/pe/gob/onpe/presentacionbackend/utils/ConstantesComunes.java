package pe.gob.onpe.presentacionbackend.utils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConstantesComunes {

    private ConstantesComunes() {
        throw new IllegalStateException("ConstantesComunes");
    }
    public static final Long CODIGO_ELECCION_CERO = 0L;

    public static final String IMG_LOGO_ONPE = "imagenes/logo-onpe.svg";

    public static final String FILE_TEST = "reportes/firmaOnpe.pdf";

    public static final String PATH_ELECCION_PRESIDENCIALL = "pe/gob/onpe/prbackend/reportes/eleccion_presidencial_geografica.jrxml";

    public static final String PATH_ELECCION_PARLAMENTO = "pe/gob/onpe/prbackend/reportes/eleccion_parlamento_geografica.jrxml";

    public static final String PATH_ELECCION_PARLAMENTO_CANDIDATO= "pe/gob/onpe/prbackend/reportes/eleccion_parlamento_candidato.jrxml";

    public static final String PATH_ELECCION_PARLAMENTO_ORGANIZACION= "pe/gob/onpe/prbackend/reportes/eleccion_parlamento_organizacion.jrxml";
    public static final String PATH_ELECCION_PARLAMENTO_RESUMENG_GENERAL= "pe/gob/onpe/prbackend/reportes/eleccion_parlamento_resumen_general.jrxml";
    public static final String PATH_ELECCION_SENADORES33 = "pe/gob/onpe/prbackend/reportes/eleccion_senadores33_geografica.jrxml";

    public static final String PATH_ELECCION_SENADORES33_CANDIDATO= "pe/gob/onpe/prbackend/reportes/eleccion_senadores33_candidato.jrxml";

    public static final String PATH_ELECCION_SENADORES33_ORGANIZACION= "pe/gob/onpe/prbackend/reportes/eleccion_senadores33_organizacion.jrxml";
    public static final String PATH_ELECCION_SENADORES33_RESUMENG_GENERAL= "pe/gob/onpe/prbackend/reportes/eleccion_senadores33_resumen_general.jrxml";

    public static final String PATH_ELECCION_PRESIDENCIAL_RESUMEN_GENERAL = "pe/gob/onpe/prbackend/reportes/eleccion_presidencial_resumen_general.jrxml";
    public static final String PATH_ELECCION_PRESIDENCIAL_ORGANIZACION = "pe/gob/onpe/prbackend/reportes/eleccion_presidencial_organizacion.jrxml";

    public static final String PATH_ELECCION_DIPUTADOS_RESUMEN_GENERAL = "pe/gob/onpe/prbackend/reportes/eleccion_diputados_resumen_general.jrxml";

    public static final String PATH_ELECCION_DIPUTADOS = "pe/gob/onpe/prbackend/reportes/eleccion_diputados.jrxml";
    public static final String PATH_ELECCION_DIPUTADOS_CANDIDATO = "pe/gob/onpe/prbackend/reportes/eleccion_diputados_candidato.jrxml";
    public static final String PATH_ELECCION_DIPUTADOS_CANDIDATO_ORGANIZACION = "pe/gob/onpe/prbackend/reportes/eleccion_diputados_candidato_organizacion.jrxml";
    public static final String PATH_ELECCION_DIPUTADOS_SINIMAGEN = "pe/gob/onpe/prbackend/reportes/eleccion_diputados_sinimagen.jrxml";

    public static final String PATH_ELECCION_SENADORES_DISTRITO_MULTIPLE = "pe/gob/onpe/prbackend/reportes/eleccion_senadores_distrito_multiple.jrxml";
    public static final String PATH_ELECCION_SENADORES_DISTRITO_MULTIPLE_CANDIDATO = "pe/gob/onpe/prbackend/reportes/eleccion_senadores_distrito_multiple_candidato.jrxml";
    public static final String PATH_ELECCION_SENADORES_DISTRITO_MULTIPLE_CANDIDATO_ORGANIZACION = "pe/gob/onpe/prbackend/reportes/eleccion_senadores_distrito_multiple_candidato_organizacion.jrxml";
    public static final String PATH_ELECCION_SENADORES_DISTRITO_MULTIPLE_RESUMEN_GENERAL = "pe/gob/onpe/prbackend/reportes/eleccion_senadores_distrito_multiple_resumen_general.jrxml";

    public static final String PATH_PARTICIPACION_CIUDADANA= "pe/gob/onpe/prbackend/reportes/participacion_ciudadana.jrxml";


    public static final String FILE_NAME_PRESIDENCIAL = "ELECCION_PRESIDENCIAL";
    public static final String FILE_NAME_DIPUTADOS = "ELECCION_DIPUTADOS";
    public static final String FILE_NAME_PARLAMENTO = "ELECCION_PARLAMENTO";
    public static final String FILE_NAME_SENADORES33 = "ELECCION_SENADORES33";
    public static final String FILE_NAME_SENADORES27 = "ELECCION_SENADORES27";

    public static final String FILE_PARTICIPACION_CIUDADANA = "PARTICIPACION_CIUDADANA";

    public static final String AGRUPOL_VOTOS_BLANCOS_CCOD = "80";

    public static final String AGRUPOL_VOTOS_NULOS_CCOD = "81";


    public static final Integer OP_ESTADO_NOPARTICIPA = 20;

    public static final String MEDIATYPE_CSV = "text/csv";
    public static final String MEDIATYPE_PDF = "application/pdf";
    
    public static final Integer ARCHIVO_ESCRUTINIO = 0;
    public static final Integer ARCHIVO_INSTALACION_SUFRAGIO = 1;
    public static final Integer ARCHIVO_RESOLUCION = 2;

    public static final Integer TIPO_ARCHIVO_ACTA_ESCRUTINIO = 1;
    public static final Integer TIPO_ARCHIVO_ACTA_INSTALACION_SUFRAGIO = 2;
    public static final Integer TIPO_ARCHIVO_ACTA_INSTALACION = 3;
    public static final Integer TIPO_ARCHIVO_ACTA_SUFRAGIO = 4;
    public static final Integer TIPO_ARCHIVO_RESOLUCION = 5;

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
