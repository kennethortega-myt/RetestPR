package pe.gob.onpe.consultaopbackend.utils;

import java.util.List;
import java.util.stream.Stream;

public class PrUtils {

    public static Integer parseStringToInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Handle the exception if needed, for example log it or rethrow a custom exception
            return null;
        }
    }

    public static Long parseStringToLong(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            // Handle the exception if needed, for example log it or rethrow a custom exception
            return null;
        }
    }

    public static List<String> cabecerasPorEleccion(Integer idEleccion, List<String> listaOrgPoliticas) {
        List<String> cabecera;

if(idEleccion == 10) {
    cabecera = List.of(
            "TIPO DE ELECCIÓN",
            "ÁMBITO",
            "DEPARTAMENTO / CONTINENTE",
            "PROVINCIA / PAÍS",
            "DISTRITO / ESTADO",
            "CENTRO POBLADO",
            "LOCAL DE VOTACIÓN",
            "NÚMERO DE MESA",
            "ESTADO DEL ACTA",
            "ELECTORES HÁBILES"
    );
} else {
    cabecera = List.of(
            "TIPO DE ELECCIÓN",
            "ÁMBITO",
            "DEPARTAMENTO / CONTINENTE",
            "PROVINCIA / PAÍS",
            "DISTRITO / ESTADO",
            "CENTRO POBLADO",
            "LOCAL DE VOTACIÓN",
            "NÚMERO DE MESA",
            "ESTADO DEL ACTA",
            "ELECTORES HÁBILES",
            "ORGANIZACION POLÍTICA",
            "CANTIDAD DE VOTOS"
    );
}



        return Stream.concat(
                        cabecera.stream(),
                        listaOrgPoliticas == null ? Stream.empty() : listaOrgPoliticas.stream()
                )
                .toList();
    }
}
