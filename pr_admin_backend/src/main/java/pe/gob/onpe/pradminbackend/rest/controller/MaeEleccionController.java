package pe.gob.onpe.pradminbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeEleccionService;
import pe.gob.onpe.pradminbackend.model.dto.maeeleccion.MaeEleccionSelectResDto;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;

import java.util.List;

@RestController
@RequestMapping("/mae-eleccion")
@RequiredArgsConstructor
public class MaeEleccionController {
	private final MaeEleccionService maeEleccionService;

	@GetMapping("/{idProceso}")
	public ResponseEntity<GenericResponse<List<MaeEleccionSelectResDto>>> obtenerTodo(@PathVariable("idProceso") Long idProceso) {
		GenericResponse<List<MaeEleccionSelectResDto>> genericResponse = new GenericResponse<>();
		genericResponse.setSuccess(Boolean.TRUE);
		genericResponse.setData(this.maeEleccionService.obtenerMaeEleccionSelectByProceso(idProceso));
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}
	
	@GetMapping("/for-create-report/{idProceso}")
	public ResponseEntity<GenericResponse<List<MaeEleccionSelectResDto>>> obtenerParaConfigurarReporte(@PathVariable("idProceso") Long idProceso) {
		GenericResponse<List<MaeEleccionSelectResDto>> genericResponse = new GenericResponse<>();
		genericResponse.setSuccess(Boolean.TRUE);
		genericResponse.setData(this.maeEleccionService.obtenerMaeEleccionSelectByProcesoForConfigReport(idProceso));
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}

}
