package pe.gob.onpe.consultaopbackend.model.bd.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VwPrActaDetalleCandidato {

	@Field(name = "n_candidato")
	private Integer id;
	
	@Field("n_lista")
	private Integer lista;
	
	@Field("n_total_votos")
	private Long votos;
}
