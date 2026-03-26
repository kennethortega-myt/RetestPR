package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FormatoDto {

	private Integer id;
	private Long 	idArchivoActa;
	private Integer correlativo;
	private Integer tipoFormato;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
	
}
