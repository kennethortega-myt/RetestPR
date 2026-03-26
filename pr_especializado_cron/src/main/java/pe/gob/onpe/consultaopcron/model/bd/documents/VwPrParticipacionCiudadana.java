package pe.gob.onpe.consultaopcron.model.bd.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
