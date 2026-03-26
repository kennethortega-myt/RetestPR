package pe.gob.onpe.presentacionbackend.model.dto.participacionciudadana;

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
public class ParticipacionCiudadanaResponseDto {

	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Integer totalElectoresHabiles;
}
