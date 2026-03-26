package pe.gob.onpe.consultaopcron.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "mae_fecha")
public class MaeFecha {
    @Id
    @Field("id")
    private Integer id;

    @Field("d_fecha_proceso")
    private Date fechaProceso;

    @Field("servicio_firma")
    private boolean servicioFirma;

    @Field("c_descripcion")
    private String cDescripcion;

    @Field("n_activo")
    private Integer activo;

    @Field("c_aud_usuario_creacion")
    private String audUsuarioCreacion;

    @Field("d_aud_fecha_creacion")
    private Date audFechaCreacion;

    @Field("c_aud_usuario_modificacion")
    private String audUsuarioModificacion;

    @Field("d_aud_fecha_modificacion")
    private Date audFechaModificacion;

    public MaeFecha() {
        super();
    }

    public MaeFecha(Integer id) {
        super();
        this.id = id;
    }

}
