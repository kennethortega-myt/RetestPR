package pe.gob.onpe.consultaopcron.model.bd.documents;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "vw_pr_senadores_distrito_multiple")
public class VwPrSenadoresDistritoElectoralMultiple extends VwPrEleccionBase  {


}
