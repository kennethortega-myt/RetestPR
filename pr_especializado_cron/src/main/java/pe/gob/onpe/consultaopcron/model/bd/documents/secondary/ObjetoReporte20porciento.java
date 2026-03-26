package pe.gob.onpe.consultaopcron.model.bd.documents.secondary;

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

    public boolean procesarGeneracion(int paso) {
        switch (paso) {
            case 1:
                if (!generacion1) {
                    generacion1 = true;
                    return true;
                }
                break;
            case 2:
                if (!generacion2) {
                    generacion2 = true;
                    return true;
                }
                break;
            case 3:
                if (!generacion3) {
                    generacion3 = true;
                    return true;
                }
                break;
            case 4:
                if (!generacion4) {
                    generacion4 = true;
                    return true;
                }
                break;
            case 5:
                if (!generacion5) {
                    generacion5 = true;
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

}
