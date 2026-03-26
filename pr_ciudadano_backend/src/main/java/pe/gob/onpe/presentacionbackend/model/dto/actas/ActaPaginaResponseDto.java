package pe.gob.onpe.presentacionbackend.model.dto.actas;

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
public class ActaPaginaResponseDto {

	private Integer paginaActual;
	private long  totalRegistros;
	private Integer totalPaginas;
	private List<ActasResponseDto> content;
	
	private Long contabilizada;
	private Long observada;
	private Long pendiente;
}