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
@Document(collection = "det_catalogo_referencia")
public class DetCatalogoReferencia extends Auditoria {

    @Id
    @Field("id")
    private Long id;

    @DBRef(lazy=true)
    @Field("o_catalogo")
    private CabCatalogo catalogo;

    @Field("c_tabla_referencia")
    private String tablaReferencia;
    
    public DetCatalogoReferencia() {
        super();
    }

    public DetCatalogoReferencia(Long id) {
        super();
        this.id = id;
    }
}
