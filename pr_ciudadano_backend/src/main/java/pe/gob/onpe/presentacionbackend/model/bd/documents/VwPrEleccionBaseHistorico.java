package pe.gob.onpe.presentacionbackend.model.bd.documents;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class VwPrEleccionBaseHistorico extends VwPrEleccionBase {

	@Field("n_id")
    private Integer id;
	
	@Field("n_acta")
    private Long acta;
	
}
