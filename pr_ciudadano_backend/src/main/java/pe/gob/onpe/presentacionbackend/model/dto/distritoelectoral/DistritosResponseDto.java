package pe.gob.onpe.presentacionbackend.model.dto.distritoelectoral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistritosResponseDto {

	private Integer codigo;
	private String nombre;
}
