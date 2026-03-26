package pe.gob.onpe.consultaopbackend.model.bd.documents;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "vw_pr_diputados")
public class VwPrDiputados extends VwPrEleccionBase {

}
