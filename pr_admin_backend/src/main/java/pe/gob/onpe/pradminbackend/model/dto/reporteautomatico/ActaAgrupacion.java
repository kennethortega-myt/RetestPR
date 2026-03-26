package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.pradminbackend.model.bd.documents.MaeAgrupacionPolitica;

@Getter
@Setter
public class ActaAgrupacion {

	private MaeAgrupacionPolitica id;
	private int total;
}
