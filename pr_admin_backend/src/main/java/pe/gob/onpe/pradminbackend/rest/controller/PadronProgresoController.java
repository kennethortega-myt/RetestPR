package pe.gob.onpe.pradminbackend.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaePadronProgreso;
import pe.gob.onpe.pradminbackend.model.bd.service.MaePadronProgresoService;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;

@RestController
@RequestMapping("/padron-progreso")
@RequiredArgsConstructor
public class PadronProgresoController {
	private final MaePadronProgresoService maePadronProgresoService;
	
	@GetMapping("/finalizo")
	public ResponseEntity<GenericResponse<Boolean>> obtenerUltimoRegistro() {
		GenericResponse<Boolean> genericResponse = new GenericResponse<>();
		MaePadronProgreso ultimoPadronProgreso = maePadronProgresoService.ultimoRegistro();
		if(ultimoPadronProgreso!=null && !ultimoPadronProgreso.getSiguiente()) {
			genericResponse.setData(true);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		} else {
			genericResponse.setData(false);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		}
		
	}
}
