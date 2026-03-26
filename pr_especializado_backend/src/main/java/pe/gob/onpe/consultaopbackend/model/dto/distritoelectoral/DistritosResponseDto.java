package pe.gob.onpe.consultaopbackend.model.dto.distritoelectoral;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistritosResponseDto {

	private Integer codigo;
	private String nombre;
}
