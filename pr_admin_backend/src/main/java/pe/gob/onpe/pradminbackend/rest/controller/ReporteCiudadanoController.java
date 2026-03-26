package pe.gob.onpe.pradminbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.pradminbackend.model.bd.documents.secondary.TabReporteAutomatico;
import pe.gob.onpe.pradminbackend.model.bd.service.AsyncReportExecutionService;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;

@Controller
@Slf4j
@RequestMapping("/reporte-ciudadano")
@RequiredArgsConstructor
public class ReporteCiudadanoController {

    private final AsyncReportExecutionService asyncReportExecutionService;

    @PostMapping("/generarreporte")
    public ResponseEntity<GenericResponse<String>> generarReporte(
            @RequestBody TabReporteAutomatico taskConfig) {

        // Llama al método asíncrono. La ejecución continuará en otro hilo.
        asyncReportExecutionService.executeReportTask(taskConfig);

        // Responde inmediatamente al cliente.
        GenericResponse<String> genericResponse = new GenericResponse<>();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setMessage("La solicitud de generación de reporte ha sido recibida y se está procesando.");
        
        return new ResponseEntity<>(genericResponse, HttpStatus.ACCEPTED);
    }
}
