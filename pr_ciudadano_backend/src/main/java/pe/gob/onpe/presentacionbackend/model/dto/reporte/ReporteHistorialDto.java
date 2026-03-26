package pe.gob.onpe.presentacionbackend.model.dto.reporte;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@Getter
@Builder
@Setter
public class ReporteHistorialDto {

    private Date fechaConsulta;
    private String estadoDescripcion;
    private Double porcentaje;
    private String idArchivo;
    private Integer numeroRegistro;

}
