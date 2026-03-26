package pe.gob.onpe.presentacionbackend.model.bd.documents;

import lombok.*;
import lombok.experimental.SuperBuilder;

import org.springframework.data.mongodb.core.mapping.Field;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VwPrActaHistorico extends VwPrActaBase {

	@Field("id")
	private Long id;

	@Field("n_acta")
	private Long nActa;

	public VwPrActaHistorico(Long id) {
		super();
		this.id = id;
	}
}
