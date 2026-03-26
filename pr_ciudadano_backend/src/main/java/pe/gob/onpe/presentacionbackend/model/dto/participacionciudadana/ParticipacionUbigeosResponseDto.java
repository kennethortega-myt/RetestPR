package pe.gob.onpe.presentacionbackend.model.dto.participacionciudadana;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipacionUbigeosResponseDto {

	private Double  porcentajeAsistentes;
	private Double  porcentajeAusentes;
	private Integer ambitoGeografico;
	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Long idLocalVotacion;
}
