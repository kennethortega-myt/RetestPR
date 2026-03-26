package pe.gob.onpe.consultaopbackend.model.bd.documents.secondary;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
public class ObjetoReporte20porciento {

    @Field("b_generacion_1")
    private boolean generacion1;

    @Field("b_generacion_2")
    private boolean generacion2;

    @Field("b_generacion_3")
    private boolean generacion3;

    @Field("b_generacion_4")
    private boolean generacion4;
    @Field("b_generacion_5")
    private boolean generacion5;

    


}
