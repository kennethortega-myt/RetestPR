package pe.gob.onpe.presentacionbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrMesaService;
import pe.gob.onpe.presentacionbackend.model.dto.mesa.FiltroMesaTotalesDto;
import pe.gob.onpe.presentacionbackend.model.dto.mesa.MesaTotalesDto;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/mesa")
@RequiredArgsConstructor
public class MesaController {

    private final VwPrMesaService mesaService;

    @GetMapping("/totales")
    public ResponseEntity<GenericResponse<MesaTotalesDto>> obtenerMesasTotales(
            @ModelAttribute @Valid FiltroMesaTotalesDto filtros) {

        GenericResponse<MesaTotalesDto> genericResponse = new GenericResponse<>();
        Optional<MesaTotalesDto> mesa = mesaService.obtenerMesasTotales(filtros);

        if (mesa.isPresent()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(mesa.get());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }


}
