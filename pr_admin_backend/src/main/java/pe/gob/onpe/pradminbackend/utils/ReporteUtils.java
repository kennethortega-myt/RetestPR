package pe.gob.onpe.pradminbackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.ReporteFiltrosCodigosDto;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.ReporteFiltrosValoresDto;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.ReporteRequest;
import pe.gob.onpe.pradminbackend.utils.enums.AmbitoGeograficoEnum;
import pe.gob.onpe.pradminbackend.utils.enums.TipoEleccionMayusculaEnum;
import pe.gob.onpe.pradminbackend.utils.enums.TipoReporteEnum;

@Slf4j
public class ReporteUtils {

    private ReporteUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getJsonFromRequest(ReporteRequest requestReporte) {
        ObjectMapper mapper = new ObjectMapper();
        String filtros = "";
        ReporteFiltrosValoresDto jsonRequest = ReporteFiltrosValoresDto.builder()
                .tipoReporte(TipoReporteEnum.obtenerDescripcion(requestReporte.getTipoReporte()))
                .tipoEleccion(TipoEleccionMayusculaEnum.obtenerDescripcion(requestReporte.getIdEleccion().longValue()))
                .ambitoGeografico(AmbitoGeograficoEnum.obtenerDescripcion(requestReporte.getIdAmbitoGeografico()))
                .ubigeoNivel1(requestReporte.getDescripcionUbigeoNivel1() != null ? requestReporte.getDescripcionUbigeoNivel1():"")
                .ubigeoNivel2(requestReporte.getDescripcionUbigeoNivel2() != null ? requestReporte.getDescripcionUbigeoNivel2():"")
                .ubigeoNivel3(requestReporte.getDescripcionUbigeoNivel3() != null ? requestReporte.getDescripcionUbigeoNivel3():"")
                .localVotacion(requestReporte.getDescripcionLocalVotacion() != null ? requestReporte.getDescripcionLocalVotacion():"")
                .build();
        try {
        filtros = mapper.writeValueAsString(jsonRequest);
        } catch (JsonProcessingException e) {
            log.error("error en getJsonFromRequest: ",e);
            return null;
        }
        return filtros;
    }

    public static String getJsonFromRequestIds(ReporteRequest requestReporte) {
        ObjectMapper mapper = new ObjectMapper();
        String filtros = "";
        ReporteFiltrosCodigosDto jsonRequest = ReporteFiltrosCodigosDto.builder()
                .tipoReporte(requestReporte.getTipoReporte())
                .tipoEleccion(requestReporte.getIdEleccion())
                .ambitoGeografico(requestReporte.getIdAmbitoGeografico())
                .ubigeoNivel1(requestReporte.getUbigeoNivel01() != null ? Integer.parseInt(requestReporte.getUbigeoNivel01()):0)
                .ubigeoNivel2(requestReporte.getUbigeoNivel02() != null ? Integer.parseInt(requestReporte.getUbigeoNivel02()):0)
                .ubigeoNivel3(requestReporte.getIdUbigeo() != null ? Integer.parseInt(requestReporte.getIdUbigeo()):0)
                .localVotacion(requestReporte.getCodigoLocalVotacion() != null ? requestReporte.getCodigoLocalVotacion().intValue():0)
                .distritoElectoral(requestReporte.getIdDistritoElectoral())
                .build();
        try {
            filtros = mapper.writeValueAsString(jsonRequest);
        } catch (JsonProcessingException e) {
            log.error("error en getJsonFromRequest: ",e);
            return null;
        }
        return filtros;
    }

    public static ReporteFiltrosValoresDto getObjectFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        ReporteFiltrosValoresDto objeto = null;
        try {
            objeto = mapper.readValue(json,ReporteFiltrosValoresDto.class);
        } catch (JsonProcessingException e) {
            log.error("error en getObjectFromJson - json: " + json , e );
            return null;
        }
        return objeto;
    }



}
