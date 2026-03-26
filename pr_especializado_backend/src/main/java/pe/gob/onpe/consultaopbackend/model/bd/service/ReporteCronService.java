package pe.gob.onpe.consultaopbackend.model.bd.service;

import org.springframework.http.ResponseEntity;
import pe.gob.onpe.consultaopbackend.model.dto.reportecron.ReporteCronResponse;

public interface ReporteCronService {
    ResponseEntity<ReporteCronResponse> generarReporteProgramado(String idReporte);
    ResponseEntity<ReporteCronResponse> generarReporteManual(String idConfiguracion);
}
