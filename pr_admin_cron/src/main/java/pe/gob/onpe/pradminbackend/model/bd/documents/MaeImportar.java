package pe.gob.onpe.pradminbackend.model.bd.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mae_importar")
public class MaeImportar extends Auditoria {
	
	@Id
	@Field("id")
	private int id;
	
	@Field(name = "c_etiqueta")
	private String etiqueta;
	
	@Field(name = "c_atributo")
	private String atributo;
	
	@Field(name = "b_exito")
	private boolean exito;
	
}
