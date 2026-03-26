package pe.gob.onpe.consultaopbackend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoReporteEnum {
    ACTAS_GENERALES(1,"Actas contabilizadas"),
    ACTAS_OBSERVADAS(2,"Actas contabilizadas - JEE");

    private final Integer codigo;
    private final String descripcion;


    public static String obtenerDescripcion(Integer codigo) {
        for (TipoReporteEnum a : values()) {
            if (a.codigo.compareTo(codigo) == 0) {
                return a.getDescripcion();
            }
        }
        return "Todos";
    }
}
