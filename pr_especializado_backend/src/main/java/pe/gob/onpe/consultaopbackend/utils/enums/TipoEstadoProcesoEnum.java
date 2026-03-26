package pe.gob.onpe.consultaopbackend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoEstadoProcesoEnum {
    PENDIENTE(1L,"PENDIENTE"),
	EN_CURSO(2L,"EN_CURSO"),
    ERROR(3L,"ERROR");

    private final Long codigo;
    private final String descripcion;

    public static String obtenerDescripcion(Long codigo) {
        for (TipoEstadoProcesoEnum a : values()) {
            if (a.codigo.compareTo(codigo) == 0) {
                return a.getDescripcion();
            }
        }
        return "Todos";
    }

    // MÉTODO ESTÁTICO PARA CONVERTIR
    public static TipoEstadoProcesoEnum fromId(int id) {
        for (TipoEstadoProcesoEnum tipo : values()) {
            if (tipo.getCodigo().intValue() == id) {
                return tipo;
            }
        }
        // Opcional: lanzar una excepción si el id no es válido
        throw new IllegalArgumentException("ID de elección no válido: " + id);
    }

}
