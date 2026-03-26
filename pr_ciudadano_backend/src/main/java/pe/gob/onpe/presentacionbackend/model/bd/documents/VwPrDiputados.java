package pe.gob.onpe.presentacionbackend.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "vw_pr_diputados")
public class VwPrDiputados extends VwPrEleccionBase {

	@Field("c_historico")
	private List<VwPrDiputadosHistorico> historico;
}
