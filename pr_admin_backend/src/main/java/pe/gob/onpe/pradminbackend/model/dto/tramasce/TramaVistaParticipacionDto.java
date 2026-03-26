package pe.gob.onpe.pradminbackend.model.dto.tramasce;

import lombok.*;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TramaVistaParticipacionDto {

    @NotNull(message = "id es obligatorio")
    private Integer idFila;
	private Integer ambitoGeografico;
	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Integer localVotacion;
	private Integer mesa;
	private Long totalElectoresHabiles;
	private Long totalAsistentes;
	private Long totalAusentes;
	private Double  porcentajeAsistentes;
	private Double  porcentajeAusentes;


}
