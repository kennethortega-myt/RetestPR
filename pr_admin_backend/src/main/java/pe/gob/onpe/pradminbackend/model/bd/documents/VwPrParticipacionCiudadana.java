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
@Document(collection = "vw_pr_participacion_ciudadana")
public class VwPrParticipacionCiudadana extends VwPrParticipacionCiudadanaBase {
	@Id
	@Field(name = "id")
	private Integer id;

	public VwPrParticipacionCiudadana(Integer id) {
		super();
		this.id = id;
	}

	@Field("c_historico")
	private List<VwPrParticipacionCiudadanaHistorico> cHistorico;
}
