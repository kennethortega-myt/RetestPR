package pe.gob.onpe.presentacionbackend.model.dto.actas;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ActaAgrupacionCandidatoRes {
	private String nombreCompleto;
	private int lista;
	private long votos;
}
