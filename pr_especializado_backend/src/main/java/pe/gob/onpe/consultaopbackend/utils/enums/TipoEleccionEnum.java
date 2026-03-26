package pe.gob.onpe.consultaopbackend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoEleccionEnum {
	PRESIDENCIAL(10L,"Presidencial"),
    DIPUTADOS(13L,"Diputados"),
    PARLAMENTO_ANDINO(12L,"Parlamento Andino"),
    SENADORES_27(14L,"Senadores Distrito Electoral Múltiple"),
    SENADORES_33(15L,"Senadores Distrito Electoral Único"),
    REVOCATORIA_DISTRITAL(7L,"Revocatoria Distrital");

    private final Long codigo;
    private final String descripcion;


    public static String obtenerDescripcion(Long codigo) {
        for (TipoEleccionEnum a : values()) {
            if (a.codigo.compareTo(codigo) == 0) {
                return a.getDescripcion();
            }
        }
        return "Todos";
    }

    public static String obtenerDescripcion(int codigo) {
        return obtenerDescripcion(Long.valueOf(codigo));
    }
}
