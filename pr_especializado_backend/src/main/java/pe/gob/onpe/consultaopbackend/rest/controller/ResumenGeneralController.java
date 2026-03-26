package pe.gob.onpe.consultaopbackend.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.consultaopbackend.model.bd.service.ResumenGeneralService;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ActaMapaCalorRequestDto;
import pe.gob.onpe.consultaopbackend.model.dto.actas.ActaMapaCalorResponseDto;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.consultaopbackend.model.dto.resumengeneral.ActaResDto;
import pe.gob.onpe.consultaopbackend.model.dto.resumengeneral.FiltroActaEleccionDto;
import pe.gob.onpe.consultaopbackend.model.dto.resumengeneral.FiltroEleccionesDto;
import pe.gob.onpe.consultaopbackend.model.dto.resumengeneral.VistaResumenGeneralDto;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/resumen-general")
public class ResumenGeneralController {

    @Autowired
    private ResumenGeneralService resumenGeneralService;
    @PostMapping("/totales")
    public ResponseEntity<GenericResponse<ActaResDto>> obtenerTotalesPorEleccion(
            @RequestBody FiltroActaEleccionDto filtroActaEleccionDto
    ) {
        GenericResponse<ActaResDto> genericResponse = new GenericResponse<>();
        Optional<ActaResDto> actaResDto = this.resumenGeneralService.obtenerTotalesPorEleccion(filtroActaEleccionDto);

        if(actaResDto.isPresent()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(actaResDto.get());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }


    @PostMapping("/mapa-calor")
    public ResponseEntity<GenericResponse<List<ActaMapaCalorResponseDto>>> obtenerMapaCalor(@RequestBody ActaMapaCalorRequestDto filtros){
        GenericResponse<List<ActaMapaCalorResponseDto>> genericResponse = new GenericResponse<>();

        List<ActaMapaCalorResponseDto> lista = resumenGeneralService.listarMapaCalor(filtros,filtros.getTipoActa()==null?"C":filtros.getTipoActa());

        if(lista.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(lista);
            genericResponse.setMessage("Se ejecutó correctamente la operación");
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }
    
    @PostMapping("/mapa-calor-observadas")
    public ResponseEntity<GenericResponse<List<ActaMapaCalorResponseDto>>> obtenerMapaCalorObservadas(@RequestBody ActaMapaCalorRequestDto filtros){
        GenericResponse<List<ActaMapaCalorResponseDto>> genericResponse = new GenericResponse<>();

        List<ActaMapaCalorResponseDto> lista = resumenGeneralService.listarMapaCalor(filtros,"H");

        if(lista.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(lista);
            genericResponse.setMessage("Se ejecutó correctamente la operación");
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }
    
    @PostMapping("/elecciones")
    public ResponseEntity<GenericResponse<List<VistaResumenGeneralDto>>> obtenerElecciones(@RequestBody FiltroEleccionesDto filtros){
        GenericResponse<List<VistaResumenGeneralDto>> genericResponse = new GenericResponse<>();

        List<VistaResumenGeneralDto> lista = resumenGeneralService.obtenerElecciones(filtros);

        if(lista.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(lista);
            genericResponse.setMessage("Se ejecutó correctamente la operación");
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }

}
