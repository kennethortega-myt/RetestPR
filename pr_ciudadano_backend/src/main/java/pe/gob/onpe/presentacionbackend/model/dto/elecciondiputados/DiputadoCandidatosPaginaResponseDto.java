package pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiputadoCandidatosPaginaResponseDto {

	private Integer paginaActual;
	private long  totalRegistros;
	private Integer totalPaginas;
	private List<ParticipanteCandidatoDiputadoDto> content;
}