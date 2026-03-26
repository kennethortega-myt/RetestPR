package pe.gob.onpe.consultaopcron.model.bd.documents.secondary;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
public class ObjetoReporte5porciento {

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
    @Field("b_generacion_11")
    private boolean generacion11;
    @Field("b_generacion_12")
    private boolean generacion12;
    @Field("b_generacion_13")
    private boolean generacion13;
    @Field("b_generacion_14")
    private boolean generacion14;
    @Field("b_generacion_15")
    private boolean generacion15;

    @Field("b_generacion_16")
    private boolean generacion16;
    @Field("b_generacion_17")
    private boolean generacion17;
    @Field("b_generacion_18")
    private boolean generacion18;
    @Field("b_generacion_19")
    private boolean generacion19;
    @Field("b_generacion_20")
    private boolean generacion20;

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
            case 11:
                if (!generacion11) {
                    generacion11 = true;
                    return true;
                }
                break;
            case 12:
                if (!generacion12) {
                    generacion12 = true;
                    return true;
                }
                break;
            case 13:
                if (!generacion13) {
                    generacion13 = true;
                    return true;
                }
                break;
            case 14:
                if (!generacion14) {
                    generacion14 = true;
                    return true;
                }
                break;
            case 15:
                if (!generacion15) {
                    generacion15 = true;
                    return true;
                }
                break;
            case 16:
                if (!generacion16) {
                    generacion16 = true;
                    return true;
                }
                break;
            case 17:
                if (!generacion17) {
                    generacion17 = true;
                    return true;
                }
                break;
            case 18:
                if (!generacion18) {
                    generacion18 = true;
                    return true;
                }
                break;
            case 19:
                if (!generacion19) {
                    generacion19 = true;
                    return true;
                }
                break;
            case 20:
                if (!generacion20) {
                    generacion20 = true;
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

}
