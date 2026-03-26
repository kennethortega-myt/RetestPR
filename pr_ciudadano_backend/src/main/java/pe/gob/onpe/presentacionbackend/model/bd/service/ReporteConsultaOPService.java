package pe.gob.onpe.presentacionbackend.model.bd.service;

import pe.gob.onpe.presentacionbackend.model.dto.reporte.ReporteAutomaticoRequestDto;
import pe.gob.onpe.presentacionbackend.model.dto.reporte.ReporteHistorialPaginado;

public interface ReporteConsultaOPService {
    ReporteHistorialPaginado listarReportesAutomaticos(ReporteAutomaticoRequestDto request, int pagina, int tamanio);
}
