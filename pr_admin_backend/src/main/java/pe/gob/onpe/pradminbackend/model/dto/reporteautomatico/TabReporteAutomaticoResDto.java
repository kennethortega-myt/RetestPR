package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TabReporteAutomaticoResDto {
	private String id;
	private Integer eleccionId;
	private String eleccion;
	private String fechaInicio;
	private String horaInicio;
	private Integer tipoReporte;
	private Integer tipoGeneracionReporte;
	private Integer tipoGeneracionReporteVal;
	private Integer estado;
	private String estadoDescripcion;

}
