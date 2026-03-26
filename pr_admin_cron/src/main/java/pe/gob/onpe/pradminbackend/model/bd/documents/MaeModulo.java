package pe.gob.onpe.pradminbackend.model.bd.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "mae_modulo")
public class MaeModulo {
    @Id
    @Field("id")
    private Long id;
    
	@Field("c_nombre")
    private String nombre;
	
	@Field("n_padre")
    private int padre;
	
	@Field("b_hijos")
    private boolean hijos;
	
	@Field("c_icono")
    private String icono;
	
	@Field("c_url")
	private String url;
	
	@Field("n_orden")
	private Integer orden;
	
	@Field("n_activo")
    private Integer activo;
	
	@Field("n_eleccion")
	private Long eleccion;

	@Field("b_principal")
	private boolean principal;
	
	@Field("c_descripcion")
	private String descripcion;
}
