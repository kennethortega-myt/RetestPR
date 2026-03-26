package pe.gob.onpe.consultaopbackend.rest.controller;


import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.consultaopbackend.model.bd.service.ReporteCronService;
import pe.gob.onpe.consultaopbackend.model.dto.reportecron.ReporteCronResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reporteProgramado")
public class ReporteCronController {

    private final ReporteCronService reporteCronService;

    @GetMapping(value = "/procesar/{idReporte}")
    public ResponseEntity<ReporteCronResponse> procesarReporteProgramado(
            @NotBlank(message = "idReporte es obligatorio")
            @PathVariable("idReporte") String idReporte) {

        return reporteCronService.generarReporteProgramado(idReporte);
    }

}
