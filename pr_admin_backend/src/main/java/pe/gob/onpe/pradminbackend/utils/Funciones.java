package pe.gob.onpe.pradminbackend.utils;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Funciones {

    public static Map<String, Object> getMapFromIoJsonwebtokenClaims(Claims claims) {
        return new HashMap<>(claims);
    }

}
