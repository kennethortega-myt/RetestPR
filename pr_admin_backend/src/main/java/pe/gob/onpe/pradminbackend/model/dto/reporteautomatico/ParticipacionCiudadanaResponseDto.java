package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipacionCiudadanaResponseDto {

	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Integer totalElectoresHabiles;
}
