package pe.gob.onpe.pradminbackend.rest.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import pe.gob.onpe.pradminbackend.model.bd.service.ResumenGeneralService;
import pe.gob.onpe.pradminbackend.model.dto.EstadoServicioDto;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.pradminbackend.security.TokenDecoder;
import pe.gob.onpe.pradminbackend.utils.PrConstantes;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@RestController
@RequestMapping("/resumen-general")
@RequiredArgsConstructor
public class ResumenGeneralController {

    private final ResumenGeneralService resumenGeneralService;
    private final TokenDecoder tokenDecoder;
    
    @PostMapping("/validar-servicio-rabbitmq")
    public ResponseEntity<GenericResponse<EstadoServicioDto>> validarRabbitMQDisponible(
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
                genericResponse.setData(resumenGeneralService.validarServicioRabitmq());
            } else {
                genericResponse.setSuccess(Boolean.FALSE);
            }
        });

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

}
