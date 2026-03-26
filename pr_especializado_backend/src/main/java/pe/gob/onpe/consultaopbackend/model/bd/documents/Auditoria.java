package pe.gob.onpe.consultaopbackend.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class Auditoria {
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

}


