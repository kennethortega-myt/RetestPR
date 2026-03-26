package pe.gob.onpe.presentacionbackend.utils.enums;

import lombok.Getter;

@Getter
public enum TipoEleccionEnum {
	REVOCATORIA_DISTRITAL(7L,"Revocatoria Distrital"),
    PRESIDENCIAL(10L,"Presidencial"),
    DIPUTADOS(13L,"Diputados"),
    PARLAMENTO_ANDINO(12L,"Parlamento Andino"),
    SENADORES_27(14L, "Senadores 27", "Senadores DEM"),
    SENADORES_33(15L, "Senadores 33", "Senadores DEU");

    private final Long codigo;
    private final String descripcion;
    private final String descripcionAlternativa;


    TipoEleccionEnum(Long codigo, String descripcion) {
        this(codigo, descripcion, descripcion);
    }

    TipoEleccionEnum(Long codigo, String descripcion, String descripcionAlternativa) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.descripcionAlternativa = descripcionAlternativa;
    }


    public static String obtenerDescripcion(Long codigo) {
        for (TipoEleccionEnum a : values()) {
            if (a.codigo.compareTo(codigo) == 0) {
                return a.getDescripcion();
            }
        }
        return "Todos";
    }

    public static String obtenerDescripcionAlternativa(int codigo) {
        Long codigoLong = (long) codigo;
        for (TipoEleccionEnum a : values()) {
            if (a.codigo.compareTo(codigoLong) == 0) {
                return a.getDescripcionAlternativa();
            }
        }
        return "Todos";
    }
}
