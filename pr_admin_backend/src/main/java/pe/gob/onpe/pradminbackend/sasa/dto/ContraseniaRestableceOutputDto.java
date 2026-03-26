package pe.gob.onpe.pradminbackend.sasa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContraseniaRestableceOutputDto {
	private Integer resultado;
	private String mensaje;
}
