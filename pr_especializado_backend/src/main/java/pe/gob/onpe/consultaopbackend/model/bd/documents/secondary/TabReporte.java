package pe.gob.onpe.consultaopbackend.model.bd.documents.secondary;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

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
	private Integer tipoReporte;

	@Field(name = "n_tipo_eleccion")
	private Integer tipoEleccion;
	
	@Field(name = "c_codigo")
	private String codigoUsuario;
	
	@Field(name = "c_filtro")
	private String filtro;

	@Field(name = "c_filtro_value")
	private String filtroValores;
	
	@Field(name = "n_estado")
	private Integer estado;

    @Field(name = "d_fecha_ultima_actualizacion")
    private Date fechaProceso;

    @Field("n_porcentaje")
    private Double porcentaje;

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

	@Override
	public String toString() {
		return "TabReporte{" +
				"id='" + id + '\'' +
				", archivo=" + archivo +
				", nTipoReporte=" + tipoReporte +
				", codigoUsuario='" + codigoUsuario + '\'' +
				", filtro='" + filtro + '\'' +
				", filtroValores='" + filtroValores + '\'' +
				", estado=" + estado +
				", nActivo=" + nActivo +
				", cAudUsuarioCreacion='" + cAudUsuarioCreacion + '\'' +
				", fechaCreacion=" + fechaCreacion +
				", cAudUsuarioModificacion='" + cAudUsuarioModificacion + '\'' +
				", dAudFechaModificacion=" + dAudFechaModificacion +
				'}';
	}
}
