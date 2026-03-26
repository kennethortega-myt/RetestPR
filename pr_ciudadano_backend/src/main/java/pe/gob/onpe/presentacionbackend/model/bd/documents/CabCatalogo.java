package pe.gob.onpe.presentacionbackend.model.bd.documents;

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
@Document(collection = "cab_catalogo")
public class CabCatalogo extends Auditoria {

    @Id
    @Field("id")
    private Long id;

    @DBRef(lazy=true)
    @Field("o_catalogo_padre")
    private CabCatalogo catalogoPadre;

    @Field("c_maestro")
    private String maestro;

    public CabCatalogo() {
        super();
    }

    public CabCatalogo(Long id) {
        super();
        this.id = id;
    }

}
