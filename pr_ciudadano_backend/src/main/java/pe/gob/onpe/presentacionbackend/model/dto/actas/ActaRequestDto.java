package pe.gob.onpe.presentacionbackend.model.dto.actas;

import com.mongodb.lang.Nullable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActaRequestDto {
	private Integer idAmbitoGeografico;
	private Long idUbigeo;
	@Nullable
	private Long codigoLocalVotacion;
	@Nullable
	private String descripcionActaResolucion;
	@Nullable
	private Boolean resueltas;
}
