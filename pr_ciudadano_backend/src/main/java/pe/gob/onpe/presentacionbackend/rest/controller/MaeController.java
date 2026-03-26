package pe.gob.onpe.presentacionbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeFecha;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaeFechaService;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;

import java.util.Optional;

@RestController
@RequestMapping("/fecha")
@RequiredArgsConstructor
public class MaeController {

    private final MaeFechaService maeFechaService;

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
}
