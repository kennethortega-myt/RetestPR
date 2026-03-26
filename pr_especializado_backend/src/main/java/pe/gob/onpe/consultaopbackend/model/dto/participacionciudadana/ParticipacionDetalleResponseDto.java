package pe.gob.onpe.consultaopbackend.model.dto.participacionciudadana;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipacionDetalleResponseDto {

	private Integer paginaActual;
	private long  totalRegistros;
	private Integer totalPaginas;
	private List<ParticipacionUbigeosResponseDto> ubigeos;

}
