package pe.gob.onpe.consultaopbackend.rest.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.consultaopbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.consultaopbackend.redis.RedisService;
import pe.gob.onpe.consultaopbackend.sasa.service.UsuarioServicio;
import pe.gob.onpe.consultaopbackend.security.TokenDecoder;
import pe.gob.onpe.consultaopbackend.utils.PrConstantes;

import java.time.Instant;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class LogoutController {

    private final UsuarioServicio usuarioServicio;
    private final TokenDecoder tokenDecoder;
    private final RedisService redisService;

    @PostMapping("cerrar-sesion")
    public ResponseEntity<GenericResponse<String>> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        String token = authorization.substring(PrConstantes.LENGTH_BEARER);
        Claims claims = this.tokenDecoder.decodeToken(token);
        GenericResponse<String> genericResponse = new GenericResponse<>();

        this.usuarioServicio.cerrarSesionActivaSasa(
                claims.get("usr", String.class)
        );

        long timeExpired = claims.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond();
        this.redisService.addToBlacklist(token, timeExpired);

        genericResponse.setSuccess(true);
        genericResponse.setMessage("Sesión cerrada.");
        return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
    }
}
