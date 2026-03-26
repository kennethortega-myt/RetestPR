package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DistritoElectoralDto {
	private Integer id;
	private Long idDistritoElectoralPadre;
	private String codigo;
	private String nombre;
	private Integer cantidadCurules;
	private Integer cantidadCandidatos;
	private Integer activo;
	private String audUsuarioCreacion;
	private String audFechaCreacion;
	private String audUsuarioModificacion;
	private String audFechaModificacion;
}