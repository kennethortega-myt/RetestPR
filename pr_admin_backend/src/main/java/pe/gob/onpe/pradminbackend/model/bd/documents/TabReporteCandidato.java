package pe.gob.onpe.pradminbackend.model.bd.documents;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@Document(collection = "tab_reporte_candidato")
public class TabReporteCandidato {

	@Id
	@Field(name = "id")
    private Integer id;

    @Field(name = "b_exito")
    private Boolean exito;

    @Field(name = "c_ruta")
    private String ruta;

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
		return "TabReporteCandidato{" +
				"id=" + id +
				", exito=" + exito +
				", ruta='" + ruta + '\'' +
				", nActivo=" + nActivo +
				", cAudUsuarioCreacion='" + cAudUsuarioCreacion + '\'' +
				", fechaCreacion=" + fechaCreacion +
				", cAudUsuarioModificacion='" + cAudUsuarioModificacion + '\'' +
				", dAudFechaModificacion=" + dAudFechaModificacion +
				'}';
	}
}
