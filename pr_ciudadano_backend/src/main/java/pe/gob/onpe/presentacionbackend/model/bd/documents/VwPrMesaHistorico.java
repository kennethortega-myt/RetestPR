package pe.gob.onpe.presentacionbackend.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VwPrMesaHistorico extends VwPrMesaBase {

	@Field(name = "id")
	private Long id;

	@Field("n_acta")
	private Long nActa;
}
