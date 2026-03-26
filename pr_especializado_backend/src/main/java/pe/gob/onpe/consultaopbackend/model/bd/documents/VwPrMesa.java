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
@Document(collection = "vw_pr_mesa")
public class VwPrMesa extends VwPrMesaBase {

    @Id
    @Field(name = "id")
    private Long id;


}
