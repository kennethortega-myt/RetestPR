package pe.gob.onpe.consultaopbackend.model.dto.actas;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.consultaopbackend.model.bd.documents.MaeAgrupacionPolitica;

@Getter
@Setter
public class ActaAgrupacion {

	private MaeAgrupacionPolitica id;
	private int total;
}
