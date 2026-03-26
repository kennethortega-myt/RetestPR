package pe.gob.onpe.pradminbackend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AmbitoGeograficoEnum {
    PERU(1,"PERÚ"),
    EXTRANJERO(2,"EXTRANJERO");

    private final Integer codigo;
    private final String descripcion;


    public static String obtenerDescripcion(Integer codigo) {
        for (AmbitoGeograficoEnum a : values()) {
            if (a.codigo.equals(codigo)) {
                return a.getDescripcion();
            }
        }
        return "Todos";
    }
}
