package pe.gob.onpe.presentacionbackend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EleccionesMenuResponse {
	private Long id;
	private String nombre;
	private int padre;
    private boolean hijos;
	private String icono;
	private Integer orden;
	private Integer idEleccion;
	private String url;
	private boolean esPrincipal;
	private String descripcion;
}
