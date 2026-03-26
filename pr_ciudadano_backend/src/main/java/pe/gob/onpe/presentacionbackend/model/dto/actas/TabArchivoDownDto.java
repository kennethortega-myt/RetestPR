package pe.gob.onpe.presentacionbackend.model.dto.actas;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TabArchivoDownDto {
	private String id;
	private Integer tipo;
	private String nombre;
	private String descripcion;
    private Date dAudFechaCreacion;
}
