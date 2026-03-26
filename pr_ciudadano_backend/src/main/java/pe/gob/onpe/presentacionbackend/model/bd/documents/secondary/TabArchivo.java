package pe.gob.onpe.presentacionbackend.model.bd.documents.secondary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tab_archivo")
public class TabArchivo {

    @Id
    @Field(name = "id")
    private String id;

    @Field("c_guid")
    private String cGuid;

    @Field("c_nombre")
    private String cNombre;

    @Field("c_nombre_original")
    private String cNombreOriginal;

    @Field("c_formato")
    private String cFormato;

    @Field("c_peso")
    private String cPeso;

    @Field("c_ruta")
    private String cRuta;

    @Field("n_activo")
    private Integer nActivo;

    @Field("c_aud_usuario_creacion")
    private String cAudUsuarioCreacion;

    @Field("d_aud_fecha_creacion")
    private Date dAudFechaCreacion;

    @Field("c_aud_usuario_modificacion")
    private String cAudUsuarioModificacion;

    @Field("d_aud_fecha_modificacion")
    private Date dAudFechaModificacion;
	
}
