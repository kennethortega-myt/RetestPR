package pe.gob.onpe.consultaopbackend.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VwPrParticipacionCiudadanaBase extends Auditoria {

    @Field(name = "c_tipo_filtro")
    private String  tipoFiltro;

    @Field(name = "n_ambito_geografico")
    private Integer ambitoGeografico;

    @Field(name = "n_ubigeo_nivel_01")
    private Integer ubigeoNivel01;

    @Field(name = "n_ubigeo_nivel_02")
    private Integer ubigeoNivel02;

    @Field(name = "n_ubigeo_nivel_03")
    private Integer ubigeoNivel03;

    @Field(name = "n_local_votacion")
    private Long    idLocalVotacion;

    @Field(name = "n_total_electores_habiles")
    private Integer totalElectoresHabiles;

    @Field(name = "n_total_asistentes")
    private Integer totalAsistentes;

    @Field(name = "n_total_ausentes")
    private Integer totalAusentes;

    @Field(name = "n_porcentaje_asistentes")
    private Double  porcentajeAsistentes;

    @Field(name = "n_porcentaje_ausentes")
    private Double  porcentajeAusentes;

}
