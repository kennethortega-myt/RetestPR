package pe.gob.onpe.presentacionbackend.model.dto.actas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActaAgrupacionCandidatoReq {
	private Long idEleccion;
	private Integer idDistritoElectoral;
	private Long idAgrupacionPolitica;
}
