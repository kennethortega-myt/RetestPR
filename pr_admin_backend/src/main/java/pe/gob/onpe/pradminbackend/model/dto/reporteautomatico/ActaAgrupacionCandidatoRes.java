package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ActaAgrupacionCandidatoRes {
	private String documentoIdentidad;
	private String nombreCompleto;
	private int lista;
	private long votos;
}
