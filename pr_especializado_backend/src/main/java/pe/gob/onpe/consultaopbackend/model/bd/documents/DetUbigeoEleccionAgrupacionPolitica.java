package pe.gob.onpe.consultaopbackend.model.bd.documents;

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
@Document(collection = "det_ubigeo_eleccion_agrupacion_politica")
public class DetUbigeoEleccionAgrupacionPolitica {

    @Id
    @Field("id")
    private Long id;

    @DBRef(lazy=true)
    @Field("o_agrupacion_politica")
    private MaeAgrupacionPolitica agrupacionPolitica;

    @DBRef(lazy=true)
    @Field("o_det_ubigeo_eleccion")
    private DetUbigeoEleccion ubigeoEleccion;

    @Field("n_posicion")
    private Integer nPosicion;

    @Field("n_estado")
    private Integer nEstado;

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

    public DetUbigeoEleccionAgrupacionPolitica() {
        super();
    }

    public DetUbigeoEleccionAgrupacionPolitica(Long id) {
        super();
        this.id = id;
        //this.nDetUbigeoEleccionAgrupacionPoliticaPk = id;
    }

}
