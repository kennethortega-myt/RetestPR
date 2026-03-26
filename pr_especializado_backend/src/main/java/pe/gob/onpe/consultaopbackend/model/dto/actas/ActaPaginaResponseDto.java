package pe.gob.onpe.consultaopbackend.model.dto.actas;

import lombok.*;

import java.util.List;

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
}