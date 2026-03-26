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
@Document(collection = "det_ubigeo_eleccion")
public class DetUbigeoEleccion {

    @Id
    @Field("id")
    private Long id;

    @DBRef(lazy=true)
    @Field("o_ubigeo")
    private MaeUbigeo ubigeo;
    
    @Field("n_id_ubigeo")
    private Long idUbigeo;
    
    @DBRef(lazy=true)
    @Field("o_eleccion")
    private MaeEleccion eleccion;
    
    @Field("n_id_eleccion")
    private Long idEleccion;

    @Field("c_codigo_eleccion")
    private String cCodigoEleccion;

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

    public DetUbigeoEleccion() {
        super();
    }

    public DetUbigeoEleccion(Long id) {
        super();
        this.id = id;
    }


}
