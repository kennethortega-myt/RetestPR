package pe.gob.onpe.consultaopbackend.model.bd.documents;

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
@Document(collection = "mae_proceso_electoral")
public class MaeProcesoElectoral {

    @Id
    @Field("id")
    private Long id;

    @Field("c_nombre")
    private String cNombre;

    @Field("c_acronimo")
    private String cAcronimo;

    @Field("d_fecha_convocatoria")
    private Date dFechaConvocatoria;

    @Field("n_tipo_ambito_electoral")
    private Long nTipoAmbitoElectoral;

    @Field("n_activo")
    private Integer nActivo;

    @Field("c_aud_usuario_creacion")
    private String cAudUsuarioCreacion;

    @Field("d_aud_fecha_creacion")
    private Date dAudFechaCreacion;

    @Field("c_aud_usuario_modificacion")
    private String cAudUsuarioModificacion;

    @Field("d_aud_fecha_modificacion")
    private Date dAudFechaModificacion;

    public MaeProcesoElectoral() {
        super();
    }

    public MaeProcesoElectoral(Long id) {
        super();
        this.id = id;
    }

}
