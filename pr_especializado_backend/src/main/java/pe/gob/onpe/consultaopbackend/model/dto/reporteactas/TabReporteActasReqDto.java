package pe.gob.onpe.consultaopbackend.model.dto.reporteactas;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class TabReporteActasReqDto {
	private String id;
	private Integer eleccionId;
	private String eleccion;
	private LocalDate fechaInicio;
	private LocalTime horaInicio;
    private String usuario;
    private Integer estado;
	private Integer tipoReporte;
	private Integer tipoGeneracionReporte;
	private Integer tipoGeneracionReporteVal;
}
