package pe.gob.onpe.consultaopcron.model.bd.documents;

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
@Document(collection = "cab_catalogo")
public class CabCatalogo {

    @Id
    @Field("id")
    private Long id;

    @DBRef(lazy = true)
    @Field("o_catalogo_padre")
    private CabCatalogo catalogoPadre;

    @Field("c_maestro")
    private String cMaestro;

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

    public CabCatalogo() {
        super();
    }

    public CabCatalogo(Long id) {
        super();
        this.id = id;
    }

}
