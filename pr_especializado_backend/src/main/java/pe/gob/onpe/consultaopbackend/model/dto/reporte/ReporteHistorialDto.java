package pe.gob.onpe.consultaopbackend.model.dto.reporte;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@ToString
@Getter
@Builder
public class ReporteHistorialDto {

    private Date fechaConsulta;
    private String tipoReporte;
    private String tipoEleccion;
    private String ambitoGeografico;
    private String porcentajeActasContabilizadas;
    private String ubigeoNivel1;
    private String ubigeoNivel2;
    private String ubigeoNivel3;
    private String localVotacion;
    private Integer estado;
    private String estadoDescripcion;
    private String idArchivo;

}
