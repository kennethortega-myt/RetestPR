package pe.gob.onpe.pradminbackend.model.bd.service.reportes;

import org.springframework.http.ResponseEntity;
import pe.gob.onpe.pradminbackend.model.dto.reportecron.ReporteCronResponse;

public interface ReporteCronService {
    ResponseEntity<ReporteCronResponse> generarReporteProgramado(String idReporte);
    ResponseEntity<ReporteCronResponse> generarReporteManual(String idReporte);

}
