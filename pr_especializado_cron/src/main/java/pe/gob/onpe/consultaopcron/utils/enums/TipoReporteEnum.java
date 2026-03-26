package pe.gob.onpe.consultaopcron.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoReporteEnum {
    ACTAS_GENERALES(1,"Actas Generales"),
    ACTAS_OBSERVADAS(2,"Actas Observadas");

    private Integer codigo;
    private String descripcion;


    public static String obtenerDescripcion(Integer codigo) {
        for (TipoReporteEnum a : values()) {
            if (a.codigo.compareTo(codigo) == 0) {
                return a.getDescripcion();
            }
        }
        return "Todos";
    }
}
