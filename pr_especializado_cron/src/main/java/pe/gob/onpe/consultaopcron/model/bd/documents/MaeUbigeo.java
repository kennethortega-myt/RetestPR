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
@Document(collection = "mae_ubigeo")
public class MaeUbigeo {

    @Id
    @Field("id")
    private Long id;

    @DBRef(lazy=true)
    @Field("o_ubigeo_padre")
    private MaeUbigeo ubigeoPadre;
    
    @Field("n_ubigeo_padre")
    private Long nUbigeoPadre;

    @Field("n_distrito_electoral")
    private Integer nDistritoElectoral;

    @Field("c_departamento")
    private String cDepartamento;
    
    @Field("c_provincia")
    private String cProvincia;
    
    @Field("c_distrito")
    private String cDistrito;
    
    @Field("c_ubigeo")
    private String cUbigeo;

    @Field("c_nombre")
    private String cNombre;
    
    @Field("n_tipo_ambito_geografico")
    private Integer nTipoAmbitoGeografico;

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


    public MaeUbigeo() {
        super();
    }

    public MaeUbigeo(Long id) {
        super();
        this.id = id;
    }

}
