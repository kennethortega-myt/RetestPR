package pe.gob.onpe.pradminbackend.model.bd.service.reportes;

import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.ActaRespuestaReporteDto;
import pe.gob.onpe.pradminbackend.model.dto.reporteautomatico.FiltroActaEleccionReporteDto;

import java.util.Optional;

public interface ResumenGeneralReporteService {
    Optional<ActaRespuestaReporteDto> obtenerTotalesPorEleccionParaReporte(FiltroActaEleccionReporteDto filtros);
}
