package pe.gob.onpe.consultaopbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeFecha;
import pe.gob.onpe.consultaopbackend.model.bd.service.MaeFechaService;
import pe.gob.onpe.consultaopbackend.model.bd.service.MaeImportarService;
import pe.gob.onpe.consultaopbackend.model.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;

import java.util.Optional;

@RestController
@RequestMapping("/fecha")
@RequiredArgsConstructor
public class MaeController {

    private final MaeFechaService maeFechaService;
    private final MaeImportarService maeImportarService;
    private final MaeProcesoElectoralService maeProcesoElectoralService;

    @GetMapping("/listarFecha")
    public ResponseEntity<GenericResponse<MaeFecha>> obtenerFecha() {
        GenericResponse<MaeFecha> genericResponse = new GenericResponse<>();
        Optional<MaeFecha> fechaOptional = maeFechaService.findById(1);

        if (fechaOptional.isPresent()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(fechaOptional.get());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/validarprocesoimportar")
    public ResponseEntity<GenericResponse<Boolean>> validarProcesoImportar() {
        GenericResponse<Boolean> genericResponse = new GenericResponse<>();
        Boolean resultImportar = maeImportarService.validarProcesoImportar();
        Boolean resultProceso = maeProcesoElectoralService.validarProcesoActivo();

        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(resultImportar && resultProceso);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

}
