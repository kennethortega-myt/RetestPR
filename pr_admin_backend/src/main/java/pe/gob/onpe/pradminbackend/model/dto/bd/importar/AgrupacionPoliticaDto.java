package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgrupacionPoliticaDto {

	private Long   	id;
	private String  codigo;
	private String  descripcion;
	private Long	tipoAgrupacionPolitica;
	private Integer estado;
	private String  ubigeoMaximo;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;

}
