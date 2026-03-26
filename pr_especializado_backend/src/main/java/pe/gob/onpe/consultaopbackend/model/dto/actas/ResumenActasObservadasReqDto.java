package pe.gob.onpe.consultaopbackend.model.dto.actas;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenActasObservadasReqDto {
	private Integer idEleccion;
	private Integer idAmbitoGeografico;
	private String ubigeoNivel01;
	private String ubigeoNivel02;
	private String idUbigeo;
	private long codigoLocalVotacion;

	private Integer idDistritoElectoral;
}
