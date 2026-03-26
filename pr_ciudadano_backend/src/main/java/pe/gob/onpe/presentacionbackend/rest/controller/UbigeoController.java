package pe.gob.onpe.presentacionbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.presentacionbackend.model.bd.service.MaeUbigeoService;
import pe.gob.onpe.presentacionbackend.model.dto.*;
import pe.gob.onpe.presentacionbackend.model.dto.response.GenericResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ubigeos")
@RequiredArgsConstructor
public class UbigeoController {

	private final MaeUbigeoService ubigeoService;
	
	@GetMapping("/departamentos")
    public ResponseEntity<GenericResponse<List<UbigeoDepartamentoDto>>> listarDepartamentos(
    		@ModelAttribute FiltroUbigeoDepartamentoDto filtro
    		) {
        GenericResponse<List<UbigeoDepartamentoDto>> genericResponse = new GenericResponse<>();
        List<UbigeoDepartamentoDto> departamentos = this.ubigeoService.listarDepartamentosPorIdEleccionII(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(departamentos);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
	
	@GetMapping("/provincias")
    public ResponseEntity<GenericResponse<List<UbigeoProvinciaDto>>> listarProvincias(
    		@ModelAttribute FiltroUbigeoProvinciaDto filtro
    		) {
        GenericResponse<List<UbigeoProvinciaDto>> genericResponse = new GenericResponse<>();
        List<UbigeoProvinciaDto> provincias = this.ubigeoService.listarProvinciasPorIdEleccionII(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(provincias);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
	
	@GetMapping("/distritos")
    public ResponseEntity<GenericResponse<List<UbigeoDistritoDto>>> listarDistritos(
    		@ModelAttribute FiltroUbigeoDistritoDto filtro
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
	@GetMapping("/locales")
	public ResponseEntity<GenericResponse<List<UbigeoLocalVotacionDto>>> listarLocales(@ModelAttribute FiltroUbigeoLocalVotacionDto filtro) {
		GenericResponse<List<UbigeoLocalVotacionDto>> genericResponse = new GenericResponse<>();
    	genericResponse.setSuccess(Boolean.TRUE);
    	genericResponse.setData(ubigeoService.listarLocalVotacionPorIdEleccion(filtro));
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}
	
	@GetMapping("/dep-prov-distritos")
    public ResponseEntity<GenericResponse<List<UbigeoDistritoDto>>> listarDepProvDistritos(@ModelAttribute FiltroUbigeoDepartamentoDto filtro) {
        GenericResponse<List<UbigeoDistritoDto>> genericResponse = new GenericResponse<>();
        List<UbigeoDistritoDto> distritos = this.ubigeoService.listarDepProvDistritosPorIdEleccionII(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(distritos);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
	
}
