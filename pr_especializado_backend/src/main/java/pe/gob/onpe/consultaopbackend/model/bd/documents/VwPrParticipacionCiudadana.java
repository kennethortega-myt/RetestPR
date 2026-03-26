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
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vw_pr_participacion_ciudadana")
public class VwPrParticipacionCiudadana extends VwPrParticipacionCiudadanaBase {
    @Id
    @Field(name = "id")
    private Integer id;

}
