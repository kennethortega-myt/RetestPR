package pe.gob.onpe.pradminbackend.model.bd.documents;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
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
@Document(collection = "vw_pr_revocatoria_distrital")
public class VwPrRevocatoriaDistrital extends VwPrEleccionBase {

	@Field("c_historico")
	private List<VwPrRevocatoriaDistritalHistorico> historico;
}
