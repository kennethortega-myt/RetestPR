package pe.gob.onpe.presentacionbackend.model.bd.documents.secondary;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
public class ObjetoReporte10porciento {

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
    @Field("b_generacion_6")
    private boolean generacion6;
    @Field("b_generacion_7")
    private boolean generacion7;
    @Field("b_generacion_8")
    private boolean generacion8;
    @Field("b_generacion_9")
    private boolean generacion9;
    @Field("b_generacion_10")
    private boolean generacion10;



}
