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
@Document(collection = "det_catalogo_estructura")
public class DetCatalogoEstructura {

    @Id
    @Field("id")
    private Long id;

    @DBRef(lazy=true)
    @Field("o_catalogo")
    private CabCatalogo catalogo;

    @Field("c_columna")
    private String cColumna;

    @Field("c_nombre")
    private String cNombre;

    @Field("n_codigo")
    private Integer nCodigo;

    @Field("c_codigo")
    private String cCodigo;

    @Field("n_orden")
    private Long nOrden;

    @Field("c_tipo")
    private String cTipo;

    @Field("c_informacion_adicional")
    private String cInformacionAdicional;

    @Field("n_obligatorio")
    private Integer nObligatorio;

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


    public DetCatalogoEstructura() {
        super();
    }

    public DetCatalogoEstructura(Long id) {
        super();
        this.id = id;
    }


}
