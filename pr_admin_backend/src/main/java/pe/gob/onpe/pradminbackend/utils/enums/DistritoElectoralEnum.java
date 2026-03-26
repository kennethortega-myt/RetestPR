package pe.gob.onpe.pradminbackend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DistritoElectoralEnum {
    DISTRITO_ELECTORAL_1(1, "AMAZONAS"),
    DISTRITO_ELECTORAL_2(2, "ÁNCASH"),
    DISTRITO_ELECTORAL_3(3, "APURÍMAC"),
    DISTRITO_ELECTORAL_4(4, "AREQUIPA"),
    DISTRITO_ELECTORAL_5(5, "AYACUCHO"),
    DISTRITO_ELECTORAL_6(6, "CAJAMARCA"),
    DISTRITO_ELECTORAL_7(7, "CALLAO"),
    DISTRITO_ELECTORAL_8(8, "CUSCO"),
    DISTRITO_ELECTORAL_9(9, "HUANCAVELICA"),
    DISTRITO_ELECTORAL_10(10,"HUÁNUCO"),
    DISTRITO_ELECTORAL_11(11,"ICA"),
    DISTRITO_ELECTORAL_12(12,"JUNÍN"),
    DISTRITO_ELECTORAL_13(13,"LA LIBERTAD"),
    DISTRITO_ELECTORAL_14(14,"LAMBAYEQUE"),
    DISTRITO_ELECTORAL_15(15,"LIMA METROPOLITANA"),
    DISTRITO_ELECTORAL_16(16,"LIMA PROVINCIAS"),
    DISTRITO_ELECTORAL_17(17,"LORETO"),
    DISTRITO_ELECTORAL_18(18,"MADRE DE DIOS"),
    DISTRITO_ELECTORAL_19(19,"MOQUEGUA"),
    DISTRITO_ELECTORAL_20(20,"PASCO"),
    DISTRITO_ELECTORAL_21(21,"PIURA"),
    DISTRITO_ELECTORAL_22(22,"PUNO"),
    DISTRITO_ELECTORAL_23(23,"SAN MARTÍN"),
    DISTRITO_ELECTORAL_24(24,"TACNA"),
    DISTRITO_ELECTORAL_25(25,"TUMBES"),
    DISTRITO_ELECTORAL_26(26,"UCAYALI"),
    DISTRITO_ELECTORAL_27(27,"RESIDENTES EN EL EXTRANJERO");

    private final Integer codigo;
    private final String descripcion;


    public static String obtenerDescripcion(Integer codigo) {
        for (DistritoElectoralEnum a : values()) {
            if (a.codigo.compareTo(codigo) == 0) {
                return a.getDescripcion();
            }
        }
        return "Todos";
    }
}
