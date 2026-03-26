package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActaAgrupacionCandidatoReq {
	private Long idEleccion;
	private Integer idDistritoElectoral;
	private Long idAgrupacionPolitica;
}
