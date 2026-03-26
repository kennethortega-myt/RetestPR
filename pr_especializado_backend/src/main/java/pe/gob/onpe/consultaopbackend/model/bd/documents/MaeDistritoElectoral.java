package pe.gob.onpe.consultaopbackend.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mae_distrito_electoral")
public class MaeDistritoElectoral extends Auditoria {
	@Id
	@Field(name = "id")
	private Integer id;
	
	@DBRef(lazy=true)
	@Field("o_distrito_electoral")
	private MaeDistritoElectoral distritoElectoralPadre;
	
	@Field(name = "c_codigo")
	private String codigo;
	
	@Field(name = "c_nombre")
	private String nombre;
	
	@Field(name = "n_cantidad_curules")
	private Integer cantidadCurules;
	
	@Field(name = "n_cantidad_candidatos")
	private Integer cantidadCandidatos;

	public MaeDistritoElectoral(Integer id) {
		super();
		this.id = id;
	}
}
