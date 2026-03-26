package pe.gob.onpe.presentacionbackend.rest.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.presentacionbackend.model.bd.service.VwPrRevocatoriaDistritalService;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.ParticipanteDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.ParticipanteReqDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.ParticipanteResDto;
import pe.gob.onpe.presentacionbackend.model.dto.revocatoria.TotalesDistritalesDto;

@RestController
@RequestMapping("/revocatoria-distrital")
@RequiredArgsConstructor
public class RevocatoriaDistritalController {

	private final VwPrRevocatoriaDistritalService vwPrRevocatoriaDistritalService;

	@GetMapping("/resumen")
	public ResponseEntity<GenericResponse<TotalesDistritalesDto>> totalesDistrital() {
		TotalesDistritalesDto totalDistrital = this.vwPrRevocatoriaDistritalService.obtenerTotales();
		if(totalDistrital == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			GenericResponse<TotalesDistritalesDto> genericResponse = new GenericResponse<>();
			genericResponse.setSuccess(true);
			genericResponse.setData(totalDistrital);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
	@GetMapping("/participantes/{cargo}")
	public ResponseEntity<GenericResponse<List<ParticipanteDto>>> listaParticipantesv1(@PathVariable String cargo) {
		List<ParticipanteDto> lstParticipantes = this.vwPrRevocatoriaDistritalService.listarParticipantesv1(cargo);
		if(lstParticipantes.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			GenericResponse<List<ParticipanteDto>> genericResponse = new GenericResponse<>();
			genericResponse.setSuccess(true);
			genericResponse.setData(lstParticipantes);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}
	
	@GetMapping("/participante")
	public ResponseEntity<GenericResponse<List<ParticipanteResDto>>> obtenerAlcalde(@ModelAttribute @Valid ParticipanteReqDto participanteReq) {
		List<ParticipanteResDto> lstParticipante = this.vwPrRevocatoriaDistritalService.listarParticipantesUbicacionGeografica(participanteReq);
		if(lstParticipante.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			GenericResponse<List<ParticipanteResDto>> genericResponse = new GenericResponse<>();
			genericResponse.setSuccess(true);
			genericResponse.setData(lstParticipante);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
	}

}
