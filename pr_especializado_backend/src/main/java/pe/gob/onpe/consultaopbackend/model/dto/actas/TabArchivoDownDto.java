package pe.gob.onpe.consultaopbackend.model.dto.actas;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TabArchivoDownDto {
	private String id;
	private Integer tipo;
	private String nombre;
	private String descripcion;
    private Date dAudFechaCreacion;
}
