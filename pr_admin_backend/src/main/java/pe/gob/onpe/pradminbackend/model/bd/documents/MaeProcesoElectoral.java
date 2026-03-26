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
@Document(collection = "mae_proceso_electoral")
public class MaeProcesoElectoral extends Auditoria {

    @Id
    @Field("id")
    private Long id;

    @Field("c_nombre")
    private String nombre;

    @Field("c_acronimo")
    private String acronimo;

    @Field("d_fecha_convocatoria")
    private Date fechaConvocatoria;

    @Field("n_tipo_ambito_electoral")
    private Long tipoAmbitoElectoral;
    
    @Field("c_tipo_proceso_electoral")
    private String tipoProcesoElectoral;

    public MaeProcesoElectoral() {
        super();
    }

    public MaeProcesoElectoral(Long id) {
        super();
        this.id = id;
    }

}
