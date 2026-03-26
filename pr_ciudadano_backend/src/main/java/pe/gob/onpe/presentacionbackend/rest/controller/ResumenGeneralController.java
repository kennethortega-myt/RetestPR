package pe.gob.onpe.presentacionbackend.rest.controller;


import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.service.ResumenGeneralService;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorRequestDto;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorResponseDto;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.presentacionbackend.model.dto.resumengeneral.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/resumen-general")
@RequiredArgsConstructor
public class ResumenGeneralController {

    private final ResumenGeneralService resumenGeneralService;
    
    private static final String MSG_EXITO = "Se ejecutó correctamente la operación";
    
    @GetMapping("/totales")
    public ResponseEntity<GenericResponse<ActaEleccionDto>> obtenerTotalesPorEleccion(
            @ModelAttribute FiltroActaEleccionDto filtroActaEleccionDto
    ) {
        GenericResponse<ActaEleccionDto> genericResponse = new GenericResponse<>();
        Optional<ActaEleccionDto> actaEleccionDto = this.resumenGeneralService.obtenerTotalesPorEleccion(filtroActaEleccionDto);

        if(actaEleccionDto.isPresent()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(actaEleccionDto.get());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/participantes")
    public ResponseEntity<GenericResponse<List<ParticipanteDto>>> listarParticipantesPorEleccion(
    		@ModelAttribute FiltroParticipanteDto filtroParticipanteDto
    ) {
        GenericResponse<List<ParticipanteDto>> genericResponse = new GenericResponse<>();
        List<ParticipanteDto> participanteDtoList = this.resumenGeneralService.listarParticipantesPorEleccion(filtroParticipanteDto);
        genericResponse.setData(participanteDtoList);
        if(participanteDtoList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            genericResponse.setSuccess(Boolean.TRUE);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }

    @GetMapping("/mapa-calor")
    public ResponseEntity<GenericResponse<List<ActaMapaCalorResponseDto>>> obtenerMapaCalor(@ModelAttribute ActaMapaCalorRequestDto filtros){
        GenericResponse<List<ActaMapaCalorResponseDto>> genericResponse = new GenericResponse<>();

        List<ActaMapaCalorResponseDto> lista = resumenGeneralService.listarMapaCalor(filtros,filtros.getTipoActa()==null?"C":filtros.getTipoActa());

        if(lista.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(lista);
            genericResponse.setMessage(MSG_EXITO);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }
    
    @GetMapping("/elecciones")
    public ResponseEntity<GenericResponse<List<VistaResumenGeneralDto>>> obtenerElecciones(@ModelAttribute FiltroEleccionesDto filtros){
        GenericResponse<List<VistaResumenGeneralDto>> genericResponse = new GenericResponse<>();

        List<VistaResumenGeneralDto> lista = resumenGeneralService.obtenerElecciones(filtros);

        if(lista.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(lista);
            genericResponse.setMessage(MSG_EXITO);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }
    
    @GetMapping("/revocatorias")
    public ResponseEntity<GenericResponse<List<VistaResumenGeneralDto>>> obtenerRevocatoriasv1(@ModelAttribute FiltroRevocatoriasDto filtros){
        GenericResponse<List<VistaResumenGeneralDto>> genericResponse = new GenericResponse<>();

        List<VistaResumenGeneralDto> lista = resumenGeneralService.obtenerRevocatoriasv1(filtros);

        if(lista.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(lista);
            genericResponse.setMessage(MSG_EXITO);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }
}
