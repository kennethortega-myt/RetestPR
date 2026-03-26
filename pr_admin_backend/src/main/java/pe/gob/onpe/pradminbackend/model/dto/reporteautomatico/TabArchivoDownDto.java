package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

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
