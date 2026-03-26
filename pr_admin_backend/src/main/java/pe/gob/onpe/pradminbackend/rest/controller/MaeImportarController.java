package pe.gob.onpe.pradminbackend.rest.controller;

import java.util.List;
import java.util.Optional;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeImportar;
import pe.gob.onpe.pradminbackend.model.bd.service.MaeImportarService;
import pe.gob.onpe.pradminbackend.model.dto.EstadoServicioDto;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.pradminbackend.security.TokenDecoder;
import pe.gob.onpe.pradminbackend.utils.PrConstantes;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/maeimportar")
@RequiredArgsConstructor
public class MaeImportarController {
	
	private final MaeImportarService maeImportarService;
	private final TokenDecoder tokenDecoder;

	@PostMapping
	public ResponseEntity<GenericResponse<List<MaeImportar>>> obtenerImportar() {
		GenericResponse<List<MaeImportar>> genericResponse = new GenericResponse<>();
		List<MaeImportar> lstMaeImportar = this.maeImportarService.findAll();
		if(!lstMaeImportar.isEmpty()) {
			genericResponse.setSuccess(Boolean.TRUE);
    		genericResponse.setData(lstMaeImportar);
			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
	}
	
	@PostMapping("/validar-servicio-basedatos")
    public ResponseEntity<GenericResponse<EstadoServicioDto>> validarBaseDatosDisponible(
			@Valid
			@RequestHeader(value = HttpHeaders.AUTHORIZATION)
			@NotBlank(message = "token es obligatorio") String tokenHeader
	) {
		String token = tokenHeader.substring(PrConstantes.LENGTH_BEARER);
		Claims claims = this.tokenDecoder.decodeToken(token);
		Optional<String> perfilAdminPr = Optional.ofNullable(claims.get(PrConstantes.PERFIL_ADMIN_PR_ATRIBUTO, String.class));
		GenericResponse<EstadoServicioDto> genericResponse = new GenericResponse<>();
		perfilAdminPr.ifPresent(perfil -> {
			if (perfil.equals(PrConstantes.PERFIL_ADMIN_PR_VALOR)) {
				genericResponse.setSuccess(Boolean.TRUE);
				genericResponse.setData(this.maeImportarService.validarServicioBasedatos());
			} else {
				genericResponse.setSuccess(Boolean.FALSE);
			}
		});

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
	
}
