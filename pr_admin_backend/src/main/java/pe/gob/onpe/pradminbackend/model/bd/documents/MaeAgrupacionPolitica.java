package pe.gob.onpe.pradminbackend.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@Document(collection = "mae_agrupacion_politica")
public class MaeAgrupacionPolitica extends Auditoria {

    @Id
    @Field("id")
    private Long id;

    @Field("c_codigo")
    private String codigo;

    @Field("c_descripcion")
    private String descripcion;

    @Field("n_tipo_agrupacion_politica")
    private Long tipoAgrupacionPolitica;

    @Field("n_estado")
    private Integer estado;

    @Field("c_ubigeo_maximo")
    private String ubigeoMaximo;

    public MaeAgrupacionPolitica() {
        super();
    }

    public MaeAgrupacionPolitica(Long id) {
        super();
        this.id = id;
    }

}
