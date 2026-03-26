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
@Document(collection = "mae_eleccion")
public class MaeEleccion extends Auditoria {

    @Id
    @Field("id")
    private Long id;

    @DBRef(lazy=true)
    @Field("o_proceso_electoral")
    private MaeProcesoElectoral procesoElectoral;

    @Field("n_principal")
    private Integer principal;
    
    @Field("c_codigo")
    private String codigo;

    @Field("c_nombre")
    private String nombre;
    
    @Field("c_nombre_vista")
    private String nombreVista;
    
    @Field("c_icono")
    private String icono;

    @Field("c_url")
    private String url;

    @Field("n_orden")
    private Integer orden;

    public MaeEleccion() {
        super();
    }

    public MaeEleccion(Long id) {
        super();
        this.id = id;
    }

}
