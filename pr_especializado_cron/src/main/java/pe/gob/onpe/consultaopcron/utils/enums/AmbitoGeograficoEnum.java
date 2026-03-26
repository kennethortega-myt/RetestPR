package pe.gob.onpe.consultaopcron.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AmbitoGeograficoEnum {
    PERU(1,"Perú"),
    EXTRANJERO(2,"Extranjero");

    private Integer codigo;
    private String descripcion;


    public static String obtenerDescripcion(Integer codigo) {
        for (AmbitoGeograficoEnum a : values()) {
            if (a.codigo.equals(codigo)) {
                return a.getDescripcion();
            }
        }
        return "Todos";
    }
}
