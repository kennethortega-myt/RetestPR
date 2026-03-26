package pe.gob.onpe.consultaopbackend.model.dto.actas;

import com.mongodb.lang.Nullable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActaReqDto {
	private Integer idEleccion;
	private Integer idAmbitoGeografico;
	private String ubigeoNivel01;
	private String ubigeoNivel02;
	private String idUbigeo;
	private Long codigoLocalVotacion;
	@Nullable
	private String codigoEstadoActa;

	private Integer idDistritoElectoral;
}
