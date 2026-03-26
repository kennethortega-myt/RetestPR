package pe.gob.onpe.presentacionbackend.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(collection = "mae_padron")
public class MaePadron {

	@Id
	@Field("id")
	private String id;

	@Field("c_mesa")
	private String mesa;
}
