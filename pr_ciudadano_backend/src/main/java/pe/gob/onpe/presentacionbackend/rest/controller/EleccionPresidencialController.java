package pe.gob.onpe.presentacionbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrPresidencialesService;
import pe.gob.onpe.presentacionbackend.model.dto.eleccionpresidencial.FiltroEleccionPresidencialDto;
import pe.gob.onpe.presentacionbackend.model.dto.eleccionpresidencial.ParticipantePresidencialDto;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/eleccion-presidencial")
@RequiredArgsConstructor
public class EleccionPresidencialController {


    private final VwPrPresidencialesService vwPrPresidencialesService;

    @GetMapping("/participantes-ubicacion-geografica")
    public ResponseEntity<GenericResponse<List<ParticipantePresidencialDto>>> listarParticipantesPorUbicacionGeografica(
            @ModelAttribute @Valid FiltroEleccionPresidencialDto filtroEleccionPresidencialDto
    ) {
        GenericResponse<List<ParticipantePresidencialDto>> genericResponse = new GenericResponse<>();
        List<ParticipantePresidencialDto> participanteDtoList = vwPrPresidencialesService.listarParticipantesUbicacionGeografica(filtroEleccionPresidencialDto);
        genericResponse.setData(participanteDtoList);
        if(participanteDtoList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            genericResponse.setSuccess(Boolean.TRUE);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }

    @GetMapping("/participantes-ubicacion-geografica-nombre")
    public ResponseEntity<GenericResponse<List<ParticipantePresidencialDto>>> listarParticipantesPorUbicacionGeograficaNombre(
            @ModelAttribute @Valid FiltroEleccionPresidencialDto filtroEleccionPresidencialDto
    ) {
        GenericResponse<List<ParticipantePresidencialDto>> genericResponse = new GenericResponse<>();
        List<ParticipantePresidencialDto> participanteDtoList = vwPrPresidencialesService.listarParticipantesUbicacionGeograficaNombre(filtroEleccionPresidencialDto);
        genericResponse.setData(participanteDtoList);
        if(participanteDtoList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            genericResponse.setSuccess(Boolean.TRUE);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }

    @GetMapping("/participantes-organizacion-politica")
    public ResponseEntity<GenericResponse<List<ParticipantePresidencialDto>>> listarParticipantesPorOrganizacionPolitica(
            @ModelAttribute  @Valid FiltroEleccionPresidencialDto filtroEleccionPresidencialDto
    ) {
        GenericResponse<List<ParticipantePresidencialDto>> genericResponse = new GenericResponse<>();
        List<ParticipantePresidencialDto> participanteDtoList = vwPrPresidencialesService.listarParticipantesOrganizacionPolitica(filtroEleccionPresidencialDto);
        genericResponse.setData(participanteDtoList);
        if(participanteDtoList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            genericResponse.setSuccess(Boolean.TRUE);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }


}
