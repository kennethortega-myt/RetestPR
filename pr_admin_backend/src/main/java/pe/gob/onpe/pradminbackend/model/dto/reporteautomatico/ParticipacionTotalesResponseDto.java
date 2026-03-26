package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipacionTotalesResponseDto {

	private Integer totalElectoresHabiles;
	private Integer totalAsistentes;
	private Integer totalAusentes;
	private Double  porcentajeAsistentes;
	private Double  porcentajeAusentes;

	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Long idLocalVotacion;
}
