package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ActaRequestConsultaOPDto {

	private Integer idEleccion;
	private Integer idAmbitoGeografico;
	private Long idUbigeo;
	private Long codigoLocalVotacion;

}
