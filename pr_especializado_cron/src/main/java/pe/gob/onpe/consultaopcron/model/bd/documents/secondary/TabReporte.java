package pe.gob.onpe.consultaopcron.model.bd.documents.secondary;

import java.util.Date;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Builder
@Document(collection = "tab_reporte")
public class TabReporte {

	@Id
	@Field(name = "id")
    private String id;
	
	@Field(name = "o_archivo")
	@DBRef(lazy=true)
	private TabArchivo archivo;
	
	@Field(name = "n_tipo_reporte")
	private Integer nTipoReporte;
	
	@Field(name = "c_codigo")
	private String codigoUsuario;
	
	@Field(name = "c_filtro")
	private String filtro;

	@Field(name = "c_filtro_value")
	private String filtroValores;
	
	@Field(name = "n_estado")
	private Integer estado;
	
	@Field(name = "n_activo")
    private Integer nActivo;
	
	@Field(name = "c_aud_usuario_creacion")
    private String cAudUsuarioCreacion;

    @Field(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Field(name = "c_aud_usuario_modificacion")
    private String cAudUsuarioModificacion;

    @Field(name = "d_aud_fecha_modificacion")
    private Date dAudFechaModificacion;
}
