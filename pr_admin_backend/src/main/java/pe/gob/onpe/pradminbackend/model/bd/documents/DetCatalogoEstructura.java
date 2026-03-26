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
@Document(collection = "det_catalogo_estructura")
public class DetCatalogoEstructura extends Auditoria {

    @Id
    @Field("id")
    private Long id;

    @DBRef(lazy=true)
    @Field("o_catalogo")
    private CabCatalogo catalogo;

    @Field("c_columna")
    private String columna;

    @Field("c_nombre")
    private String nombre;

    @Field("n_codigo")
    private Integer codigo;

    @Field("c_codigo")
    private String scodigo;

    @Field("n_orden")
    private Long orden;

    @Field("c_tipo")
    private String tipo;

    @Field("c_informacion_adicional")
    private String informacionAdicional;

    @Field("n_obligatorio")
    private Integer obligatorio;

    public DetCatalogoEstructura() {
        super();
    }

    public DetCatalogoEstructura(Long id) {
        super();
        this.id = id;
    }


}
