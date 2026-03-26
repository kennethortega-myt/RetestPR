package pe.gob.onpe.pradminbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.pradminbackend.model.bd.service.reportes.ReporteCandidatosService;
import pe.gob.onpe.pradminbackend.model.dto.reportes.GenerarCsvResponseDto;
import pe.gob.onpe.pradminbackend.model.dto.request.EleccionRequest;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;

@RestController
@RequestMapping("/eleccion")
@RequiredArgsConstructor
public class EleccionController {

    private final ReporteCandidatosService reporteCandidatosService;

    @PostMapping("/generar")
    public ResponseEntity<GenericResponse<GenerarCsvResponseDto>> generarCsvCandidatos(
            @RequestBody EleccionRequest eleccionRequest) {

        GenericResponse<GenerarCsvResponseDto> serviceResponse = reporteCandidatosService
                .generarCsvCandidatos(eleccionRequest.getIdEleccion(),
                        eleccionRequest.getUsuario());
        return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
    }
}
