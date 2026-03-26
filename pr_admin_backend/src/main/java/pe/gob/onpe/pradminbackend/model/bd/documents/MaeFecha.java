package pe.gob.onpe.pradminbackend.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@Document(collection = "mae_fecha")
public class MaeFecha extends Auditoria {
    @Id
    @Field("id")
    private Integer id;

    @Field("d_fecha_proceso")
    private Date fechaProceso;

    @Field("b_servicio_firma")
    private boolean servicioFirma;

    @Field("c_descripcion")
    private String cDescripcion;

    public MaeFecha() {
        super();
    }

    public MaeFecha(Integer id) {
        super();
        this.id = id;
    }
}
