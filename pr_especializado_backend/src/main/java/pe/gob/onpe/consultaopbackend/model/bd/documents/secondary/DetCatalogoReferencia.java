package pe.gob.onpe.consultaopbackend.model.bd.documents.secondary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "det_catalogo_referencia")
public class DetCatalogoReferencia {

    @Id
    @Field("id")
    private Long id;

    @DBRef(lazy=true)
    @Field("o_catalogo")
    private CabCatalogo catalogo;

    @Field("c_tabla_referencia")
    private String cTablaReferencia;

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

    public DetCatalogoReferencia() {
        super();
    }

    public DetCatalogoReferencia(Long id) {
        super();
        this.id = id;
    }
}
