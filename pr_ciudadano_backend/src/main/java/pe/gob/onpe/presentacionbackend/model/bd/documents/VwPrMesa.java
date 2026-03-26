package pe.gob.onpe.presentacionbackend.model.bd.documents;

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
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vw_pr_mesa")
public class VwPrMesa extends VwPrMesaBase {
	
	@Id
	@Field(name = "id")
	private Long id;

	@Field("c_historico")
	private List<VwPrMesaHistorico> cHistorico;
}
