package pe.gob.onpe.consultaopcron.model.bd.documents;

import org.springframework.data.mongodb.core.mapping.Field;

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
public class VwPrActaDetalleCandidato {

	@Field(name = "n_candidato")
	private Integer id;
	
	@Field("n_lista")
	private Integer lista;
	
	@Field("n_total_votos")
	private Long votos;
}
