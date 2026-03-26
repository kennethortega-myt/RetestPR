package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ActaResponseDetalleCandidatoDto {
	private String cDocumentoIdentidad;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String nombres;
	private String cCargo;
}
