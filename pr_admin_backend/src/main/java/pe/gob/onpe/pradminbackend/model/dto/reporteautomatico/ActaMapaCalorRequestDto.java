package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActaMapaCalorRequestDto {
	private Integer idEleccion;
	private Integer idAmbitoGeografico;
	private String tipoFiltro;
	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Integer idDistritoElectoral;
	private String tipoActa;

	private Integer codigoAgrupacionPolitica;
}
