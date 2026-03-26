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
@Document(collection = "mae_candidato")
public class MaeCandidato extends Auditoria {

	@Id
	@Field("id")
	private Integer id;
	
	@DBRef(lazy = true)
	@Field("o_distritoElectoral")
	private MaeDistritoElectoral distritoElectoral;
	
	@DBRef(lazy = true)
	@Field("o_eleccion")
	private MaeEleccion eleccion;
	
	@DBRef(lazy = true)
	@Field("o_agrupacionPolitica")
	private MaeAgrupacionPolitica agrupacionPolitica;
	
	@Field(name = "n_cargo")
	private Integer cargo;
	
	@Field(name = "c_documento_identidad")
	private String  documentoIdentidad;
	
	@Field(name = "c_apellido_paterno")
	private String  apellidoPaterno;
	
	@Field(name = "c_apellido_materno")
	private String  apellidoMaterno;
	
	@Field(name = "c_nombres")
	private String  nombres;
	
	@Field(name = "n_sexo")
	private Integer sexo;
	
	@Field(name = "n_estado")
	private Integer estado;
	
	@Field(name = "n_lista")
	private Integer lista;
	
}
