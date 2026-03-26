package pe.gob.onpe.consultaopbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@Getter
@Setter
public class ReporteHistorialRequestDto {

    private Date fechaInicio;
    private Date fechaFin;
    private String usuarioConsulta;
    private Integer tipoReporte;
    private Integer tipoEleccion;

}
