package pe.gob.onpe.pradminbackend.utils;

import java.util.Map;

public class LocalVotacionConTildeUtil {

    private LocalVotacionConTildeUtil(){

    }

    private static final Map<String, String> nombresConTilde = Map.of(
            "IE JESUS NAZARENO", "IE JESÚS NAZARENO",
            "IE 30502 ALEJANDRO SANCHEZ ARTEAGA", "IE 30502 ALEJANDRO SÁNCHEZ ARTEAGA",
            "64114 FERNANDO BELAUNDE TERRY - BOQUERON", "64114 FERNANDO BELAÚNDE TERRY - BOQUERÓN"
    );

    public static String corregirNombre(String nombre) {
        return nombresConTilde.getOrDefault(nombre, nombre);
    }
}
