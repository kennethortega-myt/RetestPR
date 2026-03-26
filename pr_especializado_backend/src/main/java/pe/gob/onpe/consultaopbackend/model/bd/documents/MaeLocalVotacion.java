package pe.gob.onpe.consultaopbackend.model.bd.documents;

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
@Document(collection = "mae_local_votacion")
public class MaeLocalVotacion extends Auditoria {

    @Id
    @Field("id")
    private Long id;

    @DBRef(lazy=true)
    @Field("o_ubigeo")
    private MaeUbigeo ubigeo;

    @Field("c_nombre")
    private String cNombre;

    @Field("c_direccion")
    private String cDireccion;

    @Field("c_referencia")
    private String cReferencia;

    @Field("c_centro_poblado")
    private String cCentroPoblado;

    @Field("n_cantidad_mesas")
    private Integer nCantidadMesas;

    @Field("n_cantidad_electorales")
    private Integer nCantidadElectores;

    @Field("n_estado")
    private Integer nEstado;

    public MaeLocalVotacion() {
        super();
    }

    public MaeLocalVotacion(Long id) {
        super();
        this.id = id;
    }
}
