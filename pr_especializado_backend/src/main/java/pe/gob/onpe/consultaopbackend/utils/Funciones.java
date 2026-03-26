package pe.gob.onpe.consultaopbackend.utils;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Funciones {

    private static final int LONGITUDIV = 12;
    private static final int LONGITUDTAG = 128;

    public static Map<String, Object> getMapFromIoJsonwebtokenClaims(Claims claims) {
        return new HashMap<>(claims);
    }

}
