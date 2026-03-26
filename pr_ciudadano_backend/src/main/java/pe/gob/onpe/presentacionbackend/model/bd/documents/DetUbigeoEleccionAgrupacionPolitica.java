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
@Document(collection = "det_ubigeo_eleccion_agrupacion_politica")
public class DetUbigeoEleccionAgrupacionPolitica extends Auditoria {

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
    private Integer posicion;

    @Field("n_estado")
    private Integer estado;

    public DetUbigeoEleccionAgrupacionPolitica() {
        super();
    }

    public DetUbigeoEleccionAgrupacionPolitica(Long id) {
        super();
        this.id = id;
    }

}
