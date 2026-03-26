package pe.gob.onpe.pradminbackend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoEleccionMayusculaEnum {
	PRESIDENCIAL(10L,"PRESIDENCIAL"),
    DIPUTADOS(13L,"DIPUTADOS"),
    PARLAMENTO_ANDINO(12L,"PARLAMENTO ANDINO"),
    SENADORES_27(14L,"SENADORES DEM"),
    SENADORES_33(15L,"SENADORES DEU"),
    REVOCATORIA_DISTRITAL(7L,"REVICATORIA DISTRITAL");

    private final Long codigo;
    private final String descripcion;


    public static String obtenerDescripcion(Long codigo) {
        for (TipoEleccionMayusculaEnum a : values()) {
            if (a.codigo.compareTo(codigo) == 0) {
                return a.getDescripcion();
            }
        }
        return "Todos";
    }

    // MÉTODO ESTÁTICO PARA CONVERTIR
    public static TipoEleccionMayusculaEnum fromId(int id) {
        for (TipoEleccionMayusculaEnum tipo : values()) {
            if (tipo.getCodigo().intValue() == id) {
                return tipo;
            }
        }
        // Opcional: lanzar una excepción si el id no es válido
        throw new IllegalArgumentException("ID de elección no válido: " + id);
    }

}
