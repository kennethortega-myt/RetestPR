package pe.gob.onpe.consultaopbackend.model.dto.parlamentoandino;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroParticipanteParlamentoAndinoDto {
	@NotNull
	private Integer idEleccion;
	@NotEmpty
	private String tipoFiltro;
	private Integer idAmbitoGeografico;
	private Integer ubigeoNivel1;
	private Integer ubigeoNivel2;
	private Integer ubigeoNivel3;
	private String nombrePartidoPolitico;

	private String nombreCandidato;
	private int idAgrupacionPolitica;
}
