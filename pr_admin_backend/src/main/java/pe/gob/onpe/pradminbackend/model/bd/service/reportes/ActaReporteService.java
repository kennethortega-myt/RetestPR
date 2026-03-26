package pe.gob.onpe.pradminbackend.model.bd.service.reportes;

import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrActa;
import pe.gob.onpe.pradminbackend.model.bd.service.CrudService;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.*;

import java.util.Optional;

public interface ActaReporteService extends CrudService<VwPrActa> {

    ReporteRespuestaDto obtenerActasReporte(ActaRequestDto filtro);
    ReporteRespuestaCsvDto obtenerActasReporteSinArchivo(ActaRequestDto filtro);
    ReporteRespuestaDto obtenerActasReporteObservadas(ActaRequestDto filtro);
    ReporteRespuestaCsvDto obtenerActasReporteObservadasCsv(ActaRequestDto filtro);
    Optional<ResumenActasObservadasResDto> obtenerResumenActasObservadas(ResumenActasObservadasReqDto filtro);}
