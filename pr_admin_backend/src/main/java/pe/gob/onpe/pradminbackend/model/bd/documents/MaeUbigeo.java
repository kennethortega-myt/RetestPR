package pe.gob.onpe.pradminbackend.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@Document(collection = "mae_ubigeo")
public class MaeUbigeo extends Auditoria {

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

    public MaeUbigeo() {
        super();
    }

    public MaeUbigeo(Long id) {
        super();
        this.id = id;
    }

}
