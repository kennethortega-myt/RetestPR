package pe.gob.onpe.pradminbackend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoEleccionEnum {
	REVOCATORIA_DISTRITAL(7L,"Revocatoria Distrital"),
    PRESIDENCIAL(10L,"Presidencial"),
    DIPUTADOS(13L,"Diputados"),
    PARLAMENTO_ANDINO(12L,"Parlamento Andino"),
    SENADORES_27(14L,"Senadores 27"),
    SENADORES_33(15L,"Senadores 33");

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
}
