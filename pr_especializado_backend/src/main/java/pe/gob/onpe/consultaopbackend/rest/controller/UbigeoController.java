package pe.gob.onpe.consultaopbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.consultaopbackend.model.bd.service.MaeUbigeoService;
import pe.gob.onpe.consultaopbackend.model.dto.*;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;

import java.util.List;

@RestController
@RequestMapping("/ubigeos")
@RequiredArgsConstructor
public class UbigeoController {

	private final MaeUbigeoService ubigeoService;

	@GetMapping("/departamentos/all")
	public ResponseEntity<GenericResponse<List<String>>> listarDepartamentos() {
		GenericResponse<List<String>> genericResponse = new GenericResponse<>();
		List<String> departamentos = this.ubigeoService.findDistinctDepartamentos();
		genericResponse.setSuccess(Boolean.TRUE);
		genericResponse.setData(departamentos);
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}
	
	@PostMapping("/departamentos")
    public ResponseEntity<GenericResponse<List<UbigeoDepartamentoDto>>> listarDepartamentos(
    		@RequestBody FiltroUbigeoDepartamentoDto filtro
    		) {
        GenericResponse<List<UbigeoDepartamentoDto>> genericResponse = new GenericResponse<>();
        List<UbigeoDepartamentoDto> departamentos = this.ubigeoService.listarDepartamentosPorIdEleccionII(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(departamentos);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
	
	@PostMapping("/provincias")
    public ResponseEntity<GenericResponse<List<UbigeoProvinciaDto>>> listarProvincias(
    		@RequestBody FiltroUbigeoProvinciaDto filtro
    		) {
        GenericResponse<List<UbigeoProvinciaDto>> genericResponse = new GenericResponse<>();
        List<UbigeoProvinciaDto> provincias = this.ubigeoService.listarProvinciasPorIdEleccionII(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(provincias);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
	
	@PostMapping("/distritos")
    public ResponseEntity<GenericResponse<List<UbigeoDistritoDto>>> listarDistritos(
    		@RequestBody FiltroUbigeoDistritoDto filtro
    		) {
        GenericResponse<List<UbigeoDistritoDto>> genericResponse = new GenericResponse<>();
        List<UbigeoDistritoDto> distritos = this.ubigeoService.listarDistritosPorIdEleccionII(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(distritos);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
	
	/**
	 * Obtener locales filtrado por IdElección y IdUbicación
	 * @param filtro
	 * @return
	 */
	@PostMapping("/locales")
	public ResponseEntity<GenericResponse<List<UbigeoLocalVotacionDto>>> listarLocales(@RequestBody FiltroUbigeoLocalVotacionDto filtro) {
		GenericResponse<List<UbigeoLocalVotacionDto>> genericResponse = new GenericResponse<>();
    	genericResponse.setSuccess(Boolean.TRUE);
    	genericResponse.setData(ubigeoService.listarLocalVotacionPorIdEleccion(filtro));
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}
	
}
