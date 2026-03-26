package pe.gob.onpe.presentacionbackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.presentacionbackend.model.dto.reporte.ReporteFiltrosValoresDto;

@Slf4j
public class ReporteUtils {

    private ReporteUtils() {
        throw new IllegalStateException("Util class");
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
