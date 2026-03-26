package pe.gob.onpe.consultaopbackend.model.dto.actas;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActaPaginaResponseConsultaOPDto {

	private Integer paginaActual;
	private long  totalRegistros;
	private Integer totalPaginas;
	private List<ActasResponseConsultaOPDto> content;
	
	private Long contabilizada;
	private Long observada;
	private Long pendiente;

	private Long electoresHabiles;
	private Long electoresAsistentes;
	private Long electoresAusentes;

	private String filtrosUtilizados;
	private Date fechaActualizacion;
}