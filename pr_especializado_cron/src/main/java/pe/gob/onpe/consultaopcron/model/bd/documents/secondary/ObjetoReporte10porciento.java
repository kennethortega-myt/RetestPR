package pe.gob.onpe.consultaopcron.model.bd.documents.secondary;

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
            case 6:
                if (!generacion6) {
                    generacion6 = true;
                    return true;
                }
                break;
            case 7:
                if (!generacion7) {
                    generacion7 = true;
                    return true;
                }
                break;
            case 8:
                if (!generacion8) {
                    generacion8 = true;
                    return true;
                }
                break;
            case 9:
                if (!generacion9) {
                    generacion9 = true;
                    return true;
                }
                break;
            case 10:
                if (!generacion10) {
                    generacion10 = true;
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

}
