package pe.gob.onpe.consultaopbackend.model.dto.elecciondiputados;

import lombok.*;

import java.util.List;

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