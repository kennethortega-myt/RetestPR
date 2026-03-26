package pe.gob.onpe.consultaopbackend.model.dto.reporteautomatico;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class TabReporteAutomaticoReqDto {
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
