package pe.gob.onpe.consultaopbackend.model.dto.actas;

import com.mongodb.lang.Nullable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActaRequestDto {
	private Integer codigoOp;
	private Integer idEleccion;
	private Integer idAmbitoGeografico;
	private String ubigeoNivel01;
	private String ubigeoNivel02;
	private String idUbigeo;
	private Long codigoLocalVotacion;
	private Integer idDistritoElectoral;
	@Nullable
	private String descripcionActaResolucion;
	@Nullable
	private Boolean resueltas;
	private String codigoEstadoActa;
}
