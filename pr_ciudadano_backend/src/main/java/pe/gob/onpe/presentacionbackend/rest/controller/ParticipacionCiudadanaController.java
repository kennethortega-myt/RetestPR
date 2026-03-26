package pe.gob.onpe.presentacionbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.service.ParticipacionCiudadanaService;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorRequestDto;
import pe.gob.onpe.presentacionbackend.model.dto.actas.ActaMapaCalorResponseDto;
import pe.gob.onpe.presentacionbackend.model.dto.participacionciudadana.*;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/participacion-ciudadana")
@RequiredArgsConstructor
public class ParticipacionCiudadanaController {

	private final ParticipacionCiudadanaService participacionCiudadanaService;
	
	private static final String MSG_EXITO = "Se ejecutó correctamente la operación";
	
	@GetMapping("/departamentos")
	public ResponseEntity<GenericResponse<List<ParticipacionCiudadanaResponseDto>>> obtenerParticipacionCiudadanaXDep(@ModelAttribute FiltroParticipacionCiudadana filtro) {
		GenericResponse<List<ParticipacionCiudadanaResponseDto>> genericResponse = new GenericResponse<>();
    	genericResponse.setSuccess(Boolean.TRUE);
    	genericResponse.setMessage(MSG_EXITO);
    	genericResponse.setData(participacionCiudadanaService.obtenerParticipacionCiudadanaXDep(filtro));
    	return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}

	@GetMapping("/totales")
	public ResponseEntity<GenericResponse<ParticipacionTotalesResponseDto>> obtenerTotales(
			@ModelAttribute @Valid FiltroParticipacionDto filtroParticipacionCiudadanaDto) {
		GenericResponse<ParticipacionTotalesResponseDto> genericResponse = new GenericResponse<>();

		Optional<ParticipacionTotalesResponseDto> registro = participacionCiudadanaService.obtenerTotales(filtroParticipacionCiudadanaDto);

		if(registro.isPresent()) {
			genericResponse.setSuccess(Boolean.TRUE);
			genericResponse.setMessage(MSG_EXITO);
			genericResponse.setData(registro.get());
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("/ubigeos")
	public ResponseEntity<GenericResponse<ParticipacionDetalleResponseDto>> obtenerUbigeos(
			@ModelAttribute @Valid FiltroParticipacionDto filtroParticipacionCiudadanaDto,
			@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "12") int tamanio) {
		GenericResponse<ParticipacionDetalleResponseDto> genericResponse = new GenericResponse<>();

		Optional<ParticipacionDetalleResponseDto> registros = participacionCiudadanaService.listarUbigeos(filtroParticipacionCiudadanaDto,pagina,tamanio);

		if(registros.isPresent()) {
			genericResponse.setSuccess(Boolean.TRUE);
			genericResponse.setMessage(MSG_EXITO);
			genericResponse.setData(registros.get());
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("/ubigeos-total")
	public ResponseEntity<GenericResponse<List<ParticipacionUbigeosResponseDto>>> obtenerUbigeosTotal(
			@ModelAttribute @Valid FiltroParticipacionDto filtroParticipacionCiudadanaDto) {
		GenericResponse<List<ParticipacionUbigeosResponseDto>> genericResponse = new GenericResponse<>();

		List<ParticipacionUbigeosResponseDto> registros = participacionCiudadanaService.listarUbigeosTotal(filtroParticipacionCiudadanaDto);

		if(!registros.isEmpty()) {
			genericResponse.setSuccess(Boolean.TRUE);
			genericResponse.setMessage(MSG_EXITO);
			genericResponse.setData(registros);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("/mapa-calor")
	public ResponseEntity<GenericResponse<List<ActaMapaCalorResponseDto>>> obtenerMapaCalor(@ModelAttribute ActaMapaCalorRequestDto filtros){
		GenericResponse<List<ActaMapaCalorResponseDto>> genericResponse = new GenericResponse<>();

		List<ActaMapaCalorResponseDto> lista = participacionCiudadanaService.listarMapaCalor(filtros);

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
