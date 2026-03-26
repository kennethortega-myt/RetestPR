package pe.gob.onpe.presentacionbackend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadosReporteEnum {
    ESTADO_REGISTRADO(0,"Registrado"),
    ESTADO_EN_PROCESO(1,"En Proceso"),
    ESTADO_TERMINADO(2,"Contabilizada"),
    ESTADO_SIN_REGISTROS_BD(3,"Sin Data en BD"),
    ESTADO_NO_SUBIDO_SFTP(4,"No subido al sftp");

    private final Integer codigo;
    private final String descripcion;


    public static String obtenerDescripcion(Integer codigo) {
        for (EstadosReporteEnum a : values()) {
            if (a.codigo.compareTo(codigo) == 0) {
                return a.getDescripcion();
            }
        }
        return "Todos";
    }
}
