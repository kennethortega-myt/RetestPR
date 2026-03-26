package pe.gob.onpe.presentacionbackend.model.bd.documents;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
@Document(collection = "vw_pr_senadores_distrito_multiple")
public class VwPrSenadoresDistritoElectoralMultiple extends VwPrEleccionBase {

	@Field("c_historico")
	private List<VwPrSenadoresDistritoElectoralMultipleHistorico> historico;
}
