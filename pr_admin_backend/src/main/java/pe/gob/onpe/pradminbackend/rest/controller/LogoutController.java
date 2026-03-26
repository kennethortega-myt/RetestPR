package pe.gob.onpe.pradminbackend.rest.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.pradminbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.pradminbackend.redis.RedisService;
import pe.gob.onpe.pradminbackend.sasa.service.UsuarioServicio;
import pe.gob.onpe.pradminbackend.security.TokenDecoder;
import pe.gob.onpe.pradminbackend.utils.PrConstantes;

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
                claims.get("dil", Integer.class).toString(),
                claims.get("sasa_token", String.class)
        );

        long timeExpired = claims.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond();
        this.redisService.addToBlacklist(token, timeExpired);

        genericResponse.setSuccess(true);
        genericResponse.setMessage("Sesión cerrada.");
        return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
    }
}
