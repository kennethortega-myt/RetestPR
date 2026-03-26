package pe.gob.onpe.presentacionbackend.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.presentacionbackend.model.bd.service.MaeDistritoElectoralService;
import pe.gob.onpe.presentacionbackend.model.dto.distritoelectoral.DistritosResponseDto;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;

@RestController
@RequestMapping("/distrito-electoral")
public class DistritoElectoralController {
	
	private final MaeDistritoElectoralService maeDistritoElectoralService;

	public DistritoElectoralController(MaeDistritoElectoralService maeDistritoElectoralService) {
		super();
		this.maeDistritoElectoralService = maeDistritoElectoralService;
	}
	
	@GetMapping("/distritos")
	public ResponseEntity<GenericResponse<List<DistritosResponseDto>>> listarDistritos() {
		GenericResponse<List<DistritosResponseDto>> genericResponse = new GenericResponse<>();
		genericResponse.setSuccess(Boolean.TRUE);
		genericResponse.setData(maeDistritoElectoralService.listarDistritos());
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}
}
