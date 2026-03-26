package pe.gob.onpe.consultaopbackend.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "mae_importar")
public class MaeImportar extends Auditoria {

    @Id
    @Field("id")
    private Long id;

    @Field("c_etiqueta")
    private String etiqueta;

    @Field("c_atributo")
    private String atributo;

    @Field("b_exito")
    private Boolean exito;

    public MaeImportar(Long id) {
        super();
        this.id = id;
    }
}
