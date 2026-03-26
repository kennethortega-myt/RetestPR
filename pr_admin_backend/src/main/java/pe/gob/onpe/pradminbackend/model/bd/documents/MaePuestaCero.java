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
@Document(collection = "mae_puesta_cero")
public class MaePuestaCero extends Auditoria {

    @Id
    private String id;

    @Field("d_fecha_inicio")
    private Date fechaInicio;

    @Field("d_fecha_fin")
    private Date fechaFin;

    @Field("c_descripcion")
    private String descripcion;

    @Field("c_usuario_sce")
    private String usuarioSce;

    public MaePuestaCero() {
        super();
    }

    public MaePuestaCero(String id) {
        super();
        this.id = id;
    }
}
