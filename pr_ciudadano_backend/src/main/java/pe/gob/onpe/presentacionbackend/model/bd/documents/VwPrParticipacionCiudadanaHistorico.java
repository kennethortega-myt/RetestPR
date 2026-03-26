package pe.gob.onpe.presentacionbackend.model.bd.documents;

import lombok.*;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VwPrParticipacionCiudadanaHistorico extends VwPrParticipacionCiudadanaBase {

	@Id
	private Integer id;

	@Field("n_acta")
	private Long nActa;

	public VwPrParticipacionCiudadanaHistorico(Integer id) {
		super();
		this.id = id;
	}
	
}
