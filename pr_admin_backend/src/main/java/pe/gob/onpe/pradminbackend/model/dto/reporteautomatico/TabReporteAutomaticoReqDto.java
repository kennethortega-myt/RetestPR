package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TabReporteAutomaticoReqDto {
	private String id;
	private Integer eleccionId;
	private String eleccion;
    private String fechaInicio;
    private String usuario;

    private String horaInicio;
	private Integer estado;
	private Integer tipoReporte;
	private Integer tipoGeneracionReporte;
	private Integer tipoGeneracionReporteVal;
}
