package pe.gob.onpe.consultaopbackend.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DigitosUtils {

    public static List<Long> obtenerUbigeos(List<Long> ubigeos, String opcion) {

        //opcion 1 : departamento
        //opcion 2 : provincias`
        List<Long> nuevosUbigeos = new ArrayList<>();
        switch (opcion) {
            case "DEPARTAMENTOS":

                nuevosUbigeos =	ubigeos.stream()
                        .filter(Objects::nonNull)
                        .filter(data -> data.compareTo(0L) != 0)
                        .map(dato -> {
                    String digitos = dato.toString();
                    String nuevoDigitos = "0";
                    if(digitos.length() == 6){
                        nuevoDigitos = digitos.substring(0,2);
                        nuevoDigitos = nuevoDigitos + "0000";
                    } else if(digitos.length() == 5){
                        nuevoDigitos = digitos.substring(0,1);
                        nuevoDigitos = nuevoDigitos + "0000";
                    }
                    return Long.valueOf(nuevoDigitos);
                }).toList();

                break;

            case "PROVINCIAS":

                nuevosUbigeos =	ubigeos.stream()
                        .filter(Objects::nonNull)
                        .filter(data -> data.compareTo(0L) != 0)
                        .map(dato -> {
                    String digitos = dato.toString();
                    String nuevoDigitos = "0";
                    if(digitos.length() == 6){
                        nuevoDigitos = digitos.substring(0,4);
                        nuevoDigitos = nuevoDigitos + "00";
                    } else if(digitos.length() == 5){
                        nuevoDigitos = digitos.substring(0,3);
                        nuevoDigitos = nuevoDigitos + "00";
                    }
                    return Long.valueOf(nuevoDigitos);
                }).toList();

                break;
        }


        return nuevosUbigeos;
    }
	
}
