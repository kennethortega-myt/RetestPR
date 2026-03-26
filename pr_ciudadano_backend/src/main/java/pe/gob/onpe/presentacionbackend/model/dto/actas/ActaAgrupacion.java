package pe.gob.onpe.presentacionbackend.model.dto.actas;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.presentacionbackend.model.bd.documents.MaeAgrupacionPolitica;

@Getter
@Setter
public class ActaAgrupacion {

	private MaeAgrupacionPolitica id;
	private int total;
}
