package pe.gob.onpe.pradminbackend.model.bd.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "vw_pr_acta")
public class VwPrActa extends VwPrActaBase {
	@Id
	@Field("id")
	private Long id;

	public VwPrActa(Long id) {
		super();
		this.id = id;
	}

	@Field("c_historico")
	private List<VwPrActaHistorico> historico;
	
}
