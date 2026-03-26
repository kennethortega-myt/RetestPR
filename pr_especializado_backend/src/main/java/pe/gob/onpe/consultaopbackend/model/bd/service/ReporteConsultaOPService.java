package pe.gob.onpe.consultaopbackend.model.bd.service;

import org.springframework.http.ResponseEntity;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteAutomaticoRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteHistorialPaginado;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteHistorialRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.reporte.ReporteRequest;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;

public interface ReporteConsultaOPService {


    ResponseEntity<GenericResponse<String>> registrarReporteBackground(ReporteRequest request);

    ReporteHistorialPaginado listarReportesPorUsuario(ReporteHistorialRequestDto request, int pagina, int tamanio);
    ReporteHistorialPaginado listarReportesAutomaticos(ReporteAutomaticoRequestDto request, int pagina, int tamanio);

}
