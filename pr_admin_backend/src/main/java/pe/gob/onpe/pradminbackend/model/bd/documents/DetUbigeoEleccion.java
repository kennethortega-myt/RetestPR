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
@Document(collection = "det_ubigeo_eleccion")
public class DetUbigeoEleccion extends Auditoria {

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
    private String codigoEleccion;

    public DetUbigeoEleccion() {
        super();
    }

    public DetUbigeoEleccion(Long id) {
        super();
        this.id = id;
    }


}
