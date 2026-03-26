package pe.gob.onpe.presentacionbackend.model.dto.parlamentoandino;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParlamentoCandidatosPaginaResponseDto {

	private Integer paginaActual;
	private long  totalRegistros;
	private Integer totalPaginas;
	private List<ParticipanteCandidatoParlamentoAndinoDto> content;
}