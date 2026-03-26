package pe.gob.onpe.consultaopbackend.model.bd.service;

import org.springframework.http.ResponseEntity;
import pe.gob.onpe.consultaopbackend.model.dto.actas.TramaScePuestaCeroDto;
import pe.gob.onpe.consultaopbackend.model.dto.reportecron.ReporteCronResponse;
import org.springframework.core.io.Resource;
import java.util.List;
import java.util.Map;

public interface ProcesamientoActasService {
    ResponseEntity<ReporteCronResponse> procesarActas(String idConfiguracion);
    Map<String, List<Map<String, String>>> listarZipsDisponibles(String tipoEleccion);
    Resource obtenerZipParaDescarga(String tipoEleccion, String region);
    ResponseEntity<TramaScePuestaCeroDto> eliminarCarpetaDescargaActas();
    ResponseEntity<ReporteCronResponse> generarReporteManual(String idConfiguracion);

}