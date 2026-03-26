package pe.gob.onpe.pradminbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import pe.gob.onpe.pradminbackend.model.bd.service.TramaSceService;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaScePuestaCeroDto;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaSceRequest;
import pe.gob.onpe.pradminbackend.model.dto.tramasce.TramaVistaResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Objects;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/trama-sce")
@RequiredArgsConstructor
public class TramaSceController {

    private final TramaSceService tramaSceService;

    @Value("${pr_admin_access_key}")
    private String prAdminAccessKey;
    
    @PostMapping("/recibir")
    public ResponseEntity<GenericResponse<TramaVistaResponse>> recibirTrama(
            @RequestBody @Valid TramaSceRequest request
            ) {

        GenericResponse<TramaVistaResponse> genericResponse = new GenericResponse<>();

        if (request == null || request.getTramasSce() == null) {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage("La trama no debe ser nulo");
            return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
        }
        log.debug("se recibio la trama en PR: " + request.getTramasSce().size());

        Optional<TramaVistaResponse> response = this.tramaSceService.recibirTrama(request.getTramasSce());

        if(response.isPresent()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(response.get());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/puesta-cero")
    public ResponseEntity<GenericResponse<TramaScePuestaCeroDto>> puestaCero(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token,
                                                                             @RequestParam String usuarioSce) {

        GenericResponse<TramaScePuestaCeroDto> genericResponse = new GenericResponse<>();

        if (!isValidAccessTokenTrama(token)) {
            genericResponse.setSuccess(Boolean.FALSE);
            return new ResponseEntity<>(genericResponse, HttpStatus.UNAUTHORIZED);
        }

        TramaScePuestaCeroDto puestaCero = tramaSceService.puestaCero(usuarioSce);
        genericResponse.setSuccess(puestaCero.isPuestaCero());
        genericResponse.setMessage(puestaCero.getMensaje());
        if(puestaCero.isPuestaCero()) {
            genericResponse.setData(puestaCero);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } else {
            genericResponse.setData(puestaCero);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public boolean isValidAccessTokenTrama(String token) {
        return Objects.equals(prAdminAccessKey, token.replace("Bearer ", ""));
    }

}
